import axios from "axios";

const BASE_URL = process.env.REACT_APP_API_URL;
console.log("API Base URL:", BASE_URL);

export const endpoints = {
  login: "auth/token",
  candidate_register: "api/candidate",
  employer_register: "api/employer",
  job: "/api/jobs",
  job_levels: "/api/job-levels",
  job_types: "/api/job-types",
  contract_types: "/api/contract-types",
  cities: "/api/cities",
  districts: "/api/districts",
  skills: "/api/skills",
  application: "/api/application",
  user: "/api/user",
  create_payment: "/api/create-payment",
  return_payment: "/api/vnpay-return",
  bill: "/api/bill",
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
