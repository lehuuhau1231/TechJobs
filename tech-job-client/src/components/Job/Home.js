import React, { useEffect, useState } from "react";
import Header from "../layout/Header";
import Apis, { endpoints } from "../../configs/Apis";
import { Button, Container } from "react-bootstrap";
import "../styles/common.css";
import { Eraser } from "lucide-react";
import Loading from "../layout/Loading";

const Home = () => {
  const [selectedJobType, setSelectedJobType] = useState(["All"]);
  const [selectedSalaryRange, setSelectedSalaryRange] = useState("Custom");
  const [selectedExperience, setSelectedExperience] = useState(["All"]);
  const [viewMode, setViewMode] = useState("grid");
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [title, setTitle] = useState(null);
  const [jobSkill, setJobSkill] = useState(null);
  const [jobLevel, setJobLevel] = useState(null);
  const [jobType, setJobType] = useState(null);
  const [contractType, setContractType] = useState(null);
  const [city, setCity] = useState(null);
  const [jobAlert, setJobAlert] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const [jobLevels, setJobLevels] = useState([]);
  const [jobTypes, setJobTypes] = useState([]);
  const [contractTypes, setContractTypes] = useState([]);
  const [cities, setCities] = useState([]);

  useEffect(() => {
    fetchJob();
    fetchJobLevels();
    fetchJobTypes();
    fetchContractTypes();
    fetchCities();
  }, []);

  useEffect(() => {
    fetchJob();
  }, [currentPage]);

  const resetFilters = () => {
    setTitle(null);
    setJobSkill(null);
    setJobType(null);
    setJobLevel(null);
    setContractType(null);
    setCity(null);
    fetchJob();
  };

  // Fetch các dữ liệu cho dropdown
  const fetchJobLevels = async () => {
    try {
      const response = await Apis.get(endpoints.job_levels);
      if (response.status === 200) {
        setJobLevels(response.data);
      }
    } catch (error) {
      console.error("Error fetching job levels:", error);
    }
  };

  const fetchJobTypes = async () => {
    try {
      const response = await Apis.get(endpoints.job_types);
      if (response.status === 200) {
        setJobTypes(response.data);
      }
    } catch (error) {
      console.error("Error fetching job types:", error);
    }
  };

  const fetchContractTypes = async () => {
    try {
      const response = await Apis.get(endpoints.contract_types);
      if (response.status === 200) {
        setContractTypes(response.data);
      }
    } catch (error) {
      console.error("Error fetching contract types:", error);
    }
  };

  const fetchCities = async () => {
    try {
      const response = await Apis.get(endpoints.cities);
      if (response.status === 200) {
        setCities(response.data);
      }
    } catch (error) {
      console.error("Error fetching cities:", error);
    }
  };

  const fetchJob = async () => {
    try {
      setLoading(true);

      let url = `${endpoints.job}?page=${currentPage}`;

      if (title) url += `&title=${title}`;
      if (jobSkill) url += `&jobSkill=${jobSkill}`;
      if (jobLevel) url += `&jobLevel=${jobLevel}`;
      if (jobType) url += `&jobType=${jobType}`;
      if (contractType) url += `&contractType=${contractType}`;
      if (city) url += `&city=${city}`;
      console.log("Fetching jobs with URL:", url);
      const response = await Apis.get(url);

      if (response.status === 200) {
        setJobs(response.data.content);
        setTotalPages(response.data.totalPages);
      } else {
        setJobAlert(true);
      }
    } catch (e) {
      console.log("Error fetching Job: ", e);
      setJobAlert(true);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    if (currentPage !== 1) {
      setCurrentPage(1);
    } else {
      fetchJob();
    }
  };

  return (
    <>
      <Header />
      <Container>
        <div style={{ display: "flex", minHeight: "calc(100vh - 60px)" }}>
          {/* Main Content - Modified job listings with 2 columns */}
          <div style={{ flex: 1, padding: "20px" }}>
            {/* Search Bar */}
            <div
              style={{
                marginBottom: "24px",
                backgroundColor: "#F9FAFB",
                borderRadius: "8px",
                padding: "16px",
              }}
            >
              <div
                style={{
                  display: "flex",
                  marginBottom: "16px",
                  gap: "8px",
                }}
              >
                <div
                  style={{
                    flex: 1,
                    display: "flex",
                    alignItems: "center",
                    backgroundColor: "#ffffff",
                    borderRadius: "4px",
                    border: "1px solid #E5E7EB",
                    padding: "0 12px",
                  }}
                >
                  <svg
                    xmlns='http://www.w3.org/2000/svg'
                    width='16'
                    height='16'
                    fill='#9CA3AF'
                    viewBox='0 0 16 16'
                    style={{ marginRight: "8px" }}
                  >
                    <path d='M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z' />
                  </svg>
                  <input
                    type='text'
                    placeholder='Tên công việc'
                    style={{
                      border: "none",
                      padding: "10px 0",
                      outline: "none",
                      width: "100%",
                      fontSize: "14px",
                    }}
                    value={title || ""}
                    onChange={(e) => setTitle(e.target.value || null)}
                  />
                </div>
                <button
                  style={{
                    backgroundColor: "#4F46E5",
                    color: "white",
                    border: "none",
                    borderRadius: "4px",
                    padding: "0 16px",
                    fontSize: "14px",
                    cursor: "pointer",
                    fontWeight: "500",
                  }}
                  onClick={handleSearch}
                >
                  Search
                </button>
              </div>

              {/* Advanced Filters Row */}
              <div
                style={{
                  display: "flex",
                  gap: "8px",
                  flexWrap: "wrap",
                }}
              >
                {/* Job Level Select */}
                <select
                  style={{
                    flex: "1 1 200px",
                    padding: "8px 12px",
                    borderRadius: "4px",
                    border: "1px solid #E5E7EB",
                    fontSize: "14px",
                    outline: "none",
                  }}
                  value={jobLevel || ""}
                  onChange={(e) => setJobLevel(e.target.value || null)}
                >
                  <option value=''>Tất cả cấp bật</option>
                  {jobLevels.map((level) => (
                    <option key={level.id} value={level.name}>
                      {level.name}
                    </option>
                  ))}
                </select>

                {/* Job Type Select */}
                <select
                  style={{
                    flex: "1 1 200px",
                    padding: "8px 12px",
                    borderRadius: "4px",
                    border: "1px solid #E5E7EB",
                    fontSize: "14px",
                    outline: "none",
                  }}
                  value={jobType || ""}
                  onChange={(e) => setJobType(e.target.value || null)}
                >
                  <option value=''>Tất cả loại công việc</option>
                  {jobTypes.map((type) => (
                    <option key={type.id} value={type.name}>
                      {type.name}
                    </option>
                  ))}
                </select>

                {/* Contract Type Select */}
                <select
                  style={{
                    flex: "1 1 200px",
                    padding: "8px 12px",
                    borderRadius: "4px",
                    border: "1px solid #E5E7EB",
                    fontSize: "14px",
                    outline: "none",
                  }}
                  value={contractType || ""}
                  onChange={(e) => setContractType(e.target.value || null)}
                >
                  <option value=''>Tất cả loại hợp đồng</option>
                  {contractTypes.map((type) => (
                    <option key={type.id} value={type.name}>
                      {type.name}
                    </option>
                  ))}
                </select>

                {/* City Select */}
                <select
                  style={{
                    flex: "1 1 200px",
                    padding: "8px 12px",
                    borderRadius: "4px",
                    border: "1px solid #E5E7EB",
                    fontSize: "14px",
                    outline: "none",
                  }}
                  value={city || ""}
                  onChange={(e) => setCity(e.target.value || null)}
                >
                  <option value=''>Tất cả thành phố</option>
                  {cities.map((cityItem) => (
                    <option key={cityItem.id} value={cityItem.name}>
                      {cityItem.name}
                    </option>
                  ))}
                </select>
                <Button className='button' onClick={resetFilters}>
                  <Eraser size={16} />
                  Xóa bộ lọc
                </Button>
              </div>
            </div>

            {/* Filter Tags */}
            <div
              style={{
                display: "flex",
                flexWrap: "wrap",
                gap: "8px",
                marginBottom: "20px",
              }}
            >
              {/* Filter Pills would go here */}
            </div>

            {/* Job Listings - Now with 2 columns */}
            <div className='container-fluid p-0'>
              {loading ? (
                <Loading />
              ) : jobs.length > 0 ? (
                <div className='row row-cols-1 row-cols-md-2 g-4'>
                  {jobs.map((job) => (
                    <div className='col' key={job.id}>
                      <div
                        style={{
                          backgroundColor: "#ffffff",
                          borderRadius: "8px",
                          padding: "16px",
                          boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)",
                          transition: "transform 0.2s, box-shadow 0.2s",
                          cursor: "pointer",
                          height: "100%",
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.transform = "translateY(-2px)";
                          e.currentTarget.style.boxShadow =
                            "0 4px 6px rgba(0, 0, 0, 0.1)";
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.transform = "translateY(0)";
                          e.currentTarget.style.boxShadow =
                            "0 1px 3px rgba(0, 0, 0, 0.1)";
                        }}
                      >
                        <div style={{ display: "flex", gap: "16px" }}>
                          {/* Company Logo */}
                          <div
                            style={{
                              width: "60px",
                              height: "60px",
                              flexShrink: 0,
                              borderRadius: "4px",
                              overflow: "hidden",
                            }}
                          >
                            <img
                              src={job.avatar}
                              alt={job.companyName}
                              style={{
                                width: "100%",
                                height: "100%",
                                objectFit: "cover",
                              }}
                              onError={(e) => {
                                e.target.onerror = null;
                                e.target.src =
                                  "https://via.placeholder.com/60x60?text=Logo";
                              }}
                            />
                          </div>

                          {/* Job Details */}
                          <div style={{ flex: 1 }}>
                            <div
                              style={{
                                display: "flex",
                                justifyContent: "space-between",
                                alignItems: "flex-start",
                              }}
                            >
                              <div>
                                <h3
                                  style={{
                                    margin: "0 0 5px",
                                    fontSize: "18px",
                                    fontWeight: "600",
                                  }}
                                >
                                  {job.title}
                                </h3>
                                <p
                                  style={{
                                    margin: "0 0 10px",
                                    color: "#4B5563",
                                    fontSize: "14px",
                                  }}
                                >
                                  {job.companyName}
                                </p>
                              </div>
                              <div>
                                <span
                                  style={{
                                    display: "block",
                                    fontWeight: "600",
                                    color: "#4F46E5",
                                    textAlign: "right",
                                    fontSize: "16px",
                                  }}
                                >
                                  ${job.salaryMin} - ${job.salaryMax}
                                </span>
                                <span
                                  style={{
                                    fontSize: "12px",
                                    color: "#6B7280",
                                    display: "block",
                                    textAlign: "right",
                                  }}
                                >
                                  Monthly
                                </span>
                              </div>
                            </div>

                            {/* Location */}
                            <div
                              style={{
                                display: "flex",
                                alignItems: "center",
                                marginBottom: "10px",
                              }}
                            >
                              <span
                                style={{
                                  fontSize: "13px",
                                  color: "#6B7280",
                                  display: "flex",
                                  alignItems: "center",
                                }}
                              >
                                <svg
                                  xmlns='http://www.w3.org/2000/svg'
                                  width='16'
                                  height='16'
                                  fill='currentColor'
                                  viewBox='0 0 16 16'
                                  style={{ marginRight: "4px" }}
                                >
                                  <path d='M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z' />
                                </svg>
                                {job.address || job.city || "Remote"}
                              </span>
                            </div>

                            {/* Skills */}
                            <div
                              style={{
                                display: "flex",
                                flexWrap: "wrap",
                                gap: "8px",
                              }}
                            >
                              {job.jobSkills?.map((skill, index) => (
                                <span
                                  key={index}
                                  style={{
                                    backgroundColor: "#F3F4F6",
                                    borderRadius: "4px",
                                    fontSize: "12px",
                                    padding: "4px 8px",
                                    color: "#4B5563",
                                    height: "24px",
                                  }}
                                >
                                  {skill}
                                </span>
                              ))}
                              <Button
                                className='button'
                                style={{
                                  marginLeft: "auto",
                                }}
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
                <div
                  style={{
                    textAlign: "center",
                    padding: "40px",
                    color: "#6B7280",
                  }}
                >
                  <p>No jobs found matching your criteria.</p>
                  <button
                    onClick={resetFilters}
                    style={{
                      backgroundColor: "transparent",
                      color: "#4F46E5",
                      border: "1px solid #4F46E5",
                      padding: "6px 12px",
                      borderRadius: "4px",
                      fontSize: "14px",
                      cursor: "pointer",
                      marginTop: "10px",
                    }}
                  >
                    Clear filters
                  </button>
                </div>
              )}

              {/* Pagination */}
              {jobs.length > 0 && totalPages > 1 && (
                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    marginTop: "32px",
                  }}
                >
                  <nav>
                    <ul className='pagination'>
                      <li
                        className={`page-item ${
                          currentPage === 1 ? "disabled" : ""
                        }`}
                      >
                        <button
                          className='page-link'
                          onClick={() => setCurrentPage(currentPage - 1)}
                          disabled={currentPage === 1}
                        >
                          Previous
                        </button>
                      </li>
                      {[...Array(totalPages).keys()].map((page) => (
                        <li
                          key={page + 1}
                          className={`page-item ${
                            currentPage === page + 1 ? "active" : ""
                          }`}
                        >
                          <button
                            className='page-link'
                            onClick={() => setCurrentPage(page + 1)}
                          >
                            {page + 1}
                          </button>
                        </li>
                      ))}
                      <li
                        className={`page-item ${
                          currentPage === totalPages ? "disabled" : ""
                        }`}
                      >
                        <button
                          className='page-link'
                          onClick={() => setCurrentPage(currentPage + 1)}
                          disabled={currentPage === totalPages}
                        >
                          Next
                        </button>
                      </li>
                    </ul>
                  </nav>
                </div>
              )}
            </div>
          </div>
        </div>
      </Container>
    </>
  );
};

export default Home;
