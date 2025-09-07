import React, { useContext, useEffect, useState, useCallback } from "react";
import { Container, Button } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import Loading from "../layout/Loading";
import { useNavigate } from "react-router-dom";
import "../styles/common.css";
import "../styles/jobRecommendation.css";
import Header from "../layout/Header";
import cookies from "react-cookies";

const JobRecommendation = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const fetchRecommendedJobs = async () => {
    try {
      setLoading(true);
      const token = cookies.load("token");
      if (!token) {
        setJobs([]);
        return;
      }
      const response = await authApis(token).get(
        endpoints.smart_recommendation
      );

      console.log("Recommended jobs response: ", response.data);
      if (response.status === 200) {
        setJobs(response.data.content || response.data);
      }
    } catch (e) {
      console.log("Error fetching recommended jobs: ", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecommendedJobs();
  }, []);

  return (
    <>
      <Header />
      <Container>
        <div className='job-recommendation-container'>
          <div className='main-content'>
            <h2 className='page-title'>Công việc phù hợp với CV bạn</h2>

            <div className='container-fluid p-0'>
              {loading ? (
                <Loading />
              ) : jobs.length > 0 ? (
                <div className='row row-cols-1 row-cols-md-2 g-4'>
                  {jobs.map((job) => (
                    <div className='col' key={job.id}>
                      <div className='job-card'>
                        <div className='job-content'>
                          <div className='company-logo'>
                            <img
                              src={job.image}
                              alt={job.companyName}
                              onError={(e) => {
                                e.target.onerror = null;
                                e.target.src =
                                  "https://via.placeholder.com/60x60?text=Logo";
                              }}
                            />
                          </div>

                          <div className='job-details'>
                            <div className='job-header'>
                              <div>
                                <h3 className='job-title'>{job.title}</h3>
                                <p className='company-name'>
                                  {job.companyName}
                                </p>
                              </div>
                              <div className='salary-info'>
                                <span className='salary-range'>
                                  ${job.salaryMin} - ${job.salaryMax}
                                </span>
                                <span className='salary-period'>Monthly</span>
                              </div>
                            </div>

                            <div className='location-info'>
                              <svg
                                xmlns='http://www.w3.org/2000/svg'
                                width='16'
                                height='16'
                                fill='currentColor'
                                viewBox='0 0 16 16'
                                className='location-icon'
                              >
                                <path d='M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z' />
                              </svg>
                              {job.address || job.city || "Remote"}
                            </div>

                            <div className='skills-container'>
                              {job.jobSkills?.map((skill, index) => (
                                <span key={index} className='skill-tag'>
                                  {skill}
                                </span>
                              ))}
                              <Button
                                className='button detail-button'
                                onClick={() =>
                                  navigate(`/job-detail/${job.id}`)
                                }
                              >
                                Chi tiết
                              </Button>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className='empty-state'>
                  <h5>Không có công việc được đề xuất</h5>
                  <p>
                    Hiện tại chúng tôi chưa có công việc phù hợp để đề xuất cho
                    bạn.
                  </p>
                  <p className='suggestion'>
                    Hãy cập nhật hồ sơ và CV của bạn để nhận được những đề xuất
                    tốt hơn.
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </Container>
    </>
  );
};

export default JobRecommendation;
