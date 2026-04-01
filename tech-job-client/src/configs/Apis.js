import axios from "axios";

const BASE_URL = process.env.REACT_APP_API_URL;

export const endpoints = {
  login: "auth/token",
  candidate_register: "api/candidate",
  employer_register: "api/employer",
  job: "/api/jobs",
  job_tracking: "/api/jobs/job-title",
  job_levels: "/api/job-levels",
  job_types: "/api/job-types",
  contract_types: "/api/contract-types",
  cities: "/api/cities",
  districts: "/api/districts",
  skills: "/api/skills",
  application: "/api/application",
  check_applied: "/api/application/check-applied",
  candidate_applied_detail: "/api/application/candidate",
  application_approve_candidate: "/api/application/approved-candidate",
  application_contacted: "/api/application/contacted",
  approve_job: "/api/jobs/application-count",
  application_pending: "/api/application/pending",
  application_status: "/api/application/status",
  user: "/api/user",
  profile: "/api/candidate/profile",
  upload_cv: "/api/candidate/cv",
  create_payment: "/api/create-payment",
  return_payment: "/api/vnpay-return",
  bill: "/api/bills",
  smart_recommendation: "/api/smart-recommendation/jobs",
  check_cv: "/api/candidate/check-cv",
  search_career: "/api/career/search-careers",
  get_recent_message: "/api/chat-session",
};

export const authApis = (token) =>
  axios.create({
    baseURL: BASE_URL,
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

export default axios.create({
  baseURL: BASE_URL,
});
