package com.lhh.techjobs;

import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.entity.CvProfile;
import com.lhh.techjobs.entity.JobRecommendGroundTruth;
import com.lhh.techjobs.repository.CvProfileRepository;
import com.lhh.techjobs.repository.JobRecommendGroundTruthRepository;
import com.lhh.techjobs.service.RecommendationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RecommendationMetricsTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private CvProfileRepository cvProfileRepository;

    @Autowired
    private JobRecommendGroundTruthRepository groundTruthRepository;

    private static final int K = 5;
    private static final int RELEVANCE_THRESHOLD = 2;
    private static final double MIN_PRECISION_THRESHOLD = 0.40;
    private static final double MIN_RECALL_THRESHOLD    = 0.40;
    private static final double MIN_F1_THRESHOLD        = 0.40;
    private static final double MIN_MAP_THRESHOLD       = 0.40;
    private static final double MIN_NDCG_THRESHOLD      = 0.40;

    @Test
    void evaluateMetricsAtK() {
        List<CvProfile> allProfiles = cvProfileRepository.findAll();

        List<Double> precisions = new ArrayList<>();
        List<Double> recalls = new ArrayList<>();
        List<Double> f1Scores = new ArrayList<>();
        List<Double> apScores = new ArrayList<>();  // Average Precision for MAP
        List<Double> ndcgScores = new ArrayList<>();  // NDCG

        int totalHits = 0;
        int totalRelevant = 0;
        int totalRecommended = 0;

        for (CvProfile cvProfile : allProfiles) {
            List<JobRecommendGroundTruth> groundTruths = groundTruthRepository
                    .findByCvProfileIdAndRelevanceScoreGreaterThanEqual(cvProfile.getId(), RELEVANCE_THRESHOLD);

            if (groundTruths.isEmpty()) {
                continue;
            }

            Set<Integer> relevantJobIds = groundTruths.stream()
                    .map(gt -> gt.getJob().getId())
                    .collect(Collectors.toSet());

            List<JobResponse> recommendations;
            try {
                recommendations = recommendationService.recommendJobs(cvProfile);
            } catch (Exception e) {
                System.out.println("Error for CV " + cvProfile.getId() + ": " + e.getMessage());
                continue;
            }

            List<Integer> recommendedJobIds = recommendations.stream()
                    .limit(K)
                    .map(JobResponse::getId)
                    .toList();

            // ── Precision@K & Recall@K ──────────────────────────────────────────
            int hits = 0;
            for (Integer jobId : recommendedJobIds) {
                if (relevantJobIds.contains(jobId)) hits++;
            }

            double precisionAtK = (double) hits / K;
            double recallAtK = (double) hits / relevantJobIds.size();

            // ── F1@K ────────────────────────────────────────────────────────────
            double f1AtK = (precisionAtK + recallAtK) == 0.0
                    ? 0.0
                    : 2.0 * precisionAtK * recallAtK / (precisionAtK + recallAtK);

            // ── Average Precision@K (for MAP) ───────────────────────────────────
            double ap = computeAveragePrecision(recommendedJobIds, relevantJobIds);

            // ── NDCG@K ──────────────────────────────────────────────────────────
            double ndcg = computeNDCG(recommendedJobIds, relevantJobIds);

            precisions.add(precisionAtK);
            recalls.add(recallAtK);
            f1Scores.add(f1AtK);
            apScores.add(ap);
            ndcgScores.add(ndcg);

            totalHits += hits;
            totalRelevant += relevantJobIds.size();
            totalRecommended += recommendedJobIds.size();

            printCvDetail(cvProfile, recommendedJobIds, relevantJobIds,
                    hits, precisionAtK, recallAtK, f1AtK, ap, ndcg);
        }

        double meanPrecisionAtK = mean(precisions);
        double meanRecallAtK = mean(recalls);
        double meanF1AtK = mean(f1Scores);
        double mapAtK = mean(apScores);
        double meanNdcgAtK = mean(ndcgScores);

        printSummary(precisions.size(), meanPrecisionAtK, meanRecallAtK, meanF1AtK, mapAtK, meanNdcgAtK,
                totalHits, totalRelevant, totalRecommended);

        assertTrue(meanPrecisionAtK >= MIN_PRECISION_THRESHOLD,
                "Mean Precision@" + K + " is lower than threshold");
        assertTrue(meanRecallAtK >= MIN_RECALL_THRESHOLD,
                "Mean Recall@" + K + " is lower than threshold");
        assertTrue(meanF1AtK >= MIN_F1_THRESHOLD,
                "Mean F1@" + K + " is lower than threshold");
        assertTrue(mapAtK >= MIN_MAP_THRESHOLD,
                "MAP@" + K + " is lower than threshold");
        assertTrue(meanNdcgAtK >= MIN_NDCG_THRESHOLD,
                "Mean NDCG@" + K + " is lower than threshold");
    }

    // ════════════════════════════════════════════════════════════════════════
    // Metric helpers
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Average Precision@K
     */
    private double computeAveragePrecision(List<Integer> ranked, Set<Integer> relevant) {
        int relevantCount = relevant.size();
        if (relevantCount == 0) return 0.0;

        double sumPrecision = 0.0;
        int hits = 0;
        for (int i = 0; i < ranked.size(); i++) {
            if (relevant.contains(ranked.get(i))) {
                hits++;
                sumPrecision += (double) hits / (i + 1);  // precision at rank (i+1)
            }
        }
        return sumPrecision / relevantCount;
    }

    /**
     * NDCG@K with binary relevance:
     *   DCG  = Σ_{i=1..K} rel_i / log2(i+1)
     *   IDCG = DCG of ideal ranking (all relevant items first)
     */
    private double computeNDCG(List<Integer> ranked, Set<Integer> relevant) {
        double dcg  = 0.0;
        for (int i = 0; i < ranked.size(); i++) {
            if (relevant.contains(ranked.get(i))) {
                dcg += 1.0 / (Math.log(i + 2) / Math.log(2));  // log2(i+2)
            }
        }

        // IDCG: place all relevant items at the top positions (up to K)
        int idealHits = Math.min(relevant.size(), K);
        double idcg = 0.0;
        for (int i = 0; i < idealHits; i++) {
            idcg += 1.0 / (Math.log(i + 2) / Math.log(2));
        }

        return idcg == 0.0 ? 0.0 : dcg / idcg;
    }

    private double mean(List<Double> values) {
        return values.isEmpty() ? 0.0
                : values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Print helpers
    // ════════════════════════════════════════════════════════════════════════

    private void printCvDetail(CvProfile cvProfile, List<Integer> recommendedJobIds,
                                Set<Integer> relevantJobIds, int hits,
                                double precisionAtK, double recallAtK,
                                double f1AtK, double ap, double ndcg) {
        System.out.println("CV " + cvProfile.getId() + " - " + truncate(cvProfile.getTitle(), 40));
        System.out.println("  Recommended : " + recommendedJobIds);
        System.out.println("  Relevant    : " + relevantJobIds);
        System.out.printf ("  Hits=%d  P@%d=%.4f  R@%d=%.4f  F1@%d=%.4f  AP@%d=%.4f  NDCG@%d=%.4f%n",
                hits, K, precisionAtK, K, recallAtK, K, f1AtK, K, ap, K, ndcg);
        System.out.println("------------------------------------------");
    }

    private void printSummary(int totalCvs, double meanPrecisionAtK, double meanRecallAtK,
                               double meanF1AtK, double mapAtK, double meanNdcgAtK,
                               int totalHits, int totalRelevant, int totalRecommended) {
        System.out.println("==================== SUMMARY ====================");
        System.out.println("  Total CVs: " + totalCvs);
        System.out.println("  Total Hits: " + totalHits);
        System.out.println("  Total Relevant: " + totalRelevant);
        System.out.println("  Total Recommended: " + totalRecommended);
        System.out.printf("  Mean Precision@%d: %.4f%n", K, meanPrecisionAtK);
        System.out.printf("  Mean Recall@%d: %.4f%n", K, meanRecallAtK);
        System.out.printf("  Mean F1@%d: %.4f%n", K, meanF1AtK);
        System.out.printf("  MAP@%d: %.4f%n", K, mapAtK);
        System.out.printf("  Mean NDCG@%d: %.4f%n", K, meanNdcgAtK);
        System.out.println("=================================================");
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "N/A";
        return str.length() <= maxLen ? str : str.substring(0, maxLen - 3) + "...";
    }
}
