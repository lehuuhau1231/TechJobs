import React, { useEffect, useState, useRef, useCallback } from "react";
import Header from "../layout/Header";
import Apis, { endpoints } from "../../configs/Apis";
import {
  Button,
  Container,
  Row,
  Col,
  Card,
  Badge,
  Pagination,
} from "react-bootstrap";
import "../styles/common.css";
import "./home/Home.css";
import Loading from "../layout/Loading";
import { useNavigate } from "react-router-dom";
import ChatbotCareerRecommend from "../Candidate/ChatbotCareerRecommend/ChatbotCareerRecommend";
import JobFilter from "./home/JobFilter";

const Home = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [jobAlert, setJobAlert] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const filtersRef = useRef({});
  const navigate = useNavigate();

  const fetchJob = useCallback(async (filters = filtersRef.current, page = currentPage) => {
    try {
      setLoading(true);

      let url = `${endpoints.job}?page=${page}`;

      if (filters.title) url += `&title=${filters.title}`;
      if (filters.jobSkill) url += `&jobSkill=${filters.jobSkill}`;
      if (filters.jobLevel) url += `&jobLevel=${filters.jobLevel}`;
      if (filters.jobType) url += `&jobType=${filters.jobType}`;
      if (filters.contractType) url += `&contractType=${filters.contractType}`;
      if (filters.city) url += `&city=${filters.city}`;

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
  }, [currentPage]);

  useEffect(() => {
    fetchJob();
  }, []);

  useEffect(() => {
    fetchJob(filtersRef.current, currentPage);
  }, [currentPage]);

  const handleSearch = useCallback((filters) => {
    filtersRef.current = filters;
    if (currentPage !== 1) {
      setCurrentPage(1);
    } else {
      fetchJob(filters);
    }
  }, [currentPage, fetchJob]);

  const handleReset = useCallback(() => {
    filtersRef.current = {};
    if (currentPage !== 1) {
      setCurrentPage(1);
    } else {
      fetchJob({});
    }
  }, [currentPage, fetchJob]);

  const renderPagination = () => {
    const items = [];
    for (let page = 1; page <= totalPages; page++) {
      items.push(
        <Pagination.Item
          key={page}
          active={currentPage === page}
          onClick={() => setCurrentPage(page)}
        >
          {page}
        </Pagination.Item>,
      );
    }
    return items;
  };

  return (
    <>
      <Header />
      <Container>
        <div className='home-layout'>
          <div className='home-main'>
            <JobFilter onSearch={handleSearch} onReset={handleReset} />

            {/* Job Listings */}
            <Container fluid className='p-0'>
              {loading ? (
                <Loading />
              ) : jobs.length > 0 ? (
                <Row xs={1} md={2} className='g-4'>
                  {jobs.map((job) => (
                    <Col key={job.id}>
                      <Card className='home-job-card'>
                        <Card.Body className='home-job-card-body'>
                          {/* Company Logo */}
                          <div className='home-company-logo'>
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

                          {/* Job Details */}
                          <div className='home-job-details'>
                            <div className='home-job-header'>
                              <div>
                                <h3 className='home-job-title'>{job.title}</h3>
                                <p className='home-job-company'>
                                  {job.companyName}
                                </p>
                              </div>
                              <div>
                                <span className='home-job-salary'>
                                  ${job.salaryMin} - ${job.salaryMax}
                                </span>
                                <span className='home-job-salary-period'>
                                  Monthly
                                </span>
                              </div>
                            </div>

                            {/* Location */}
                            <div className='home-job-location'>
                              <span className='home-job-location-text'>
                                <svg
                                  xmlns='http://www.w3.org/2000/svg'
                                  width='16'
                                  height='16'
                                  fill='currentColor'
                                  viewBox='0 0 16 16'
                                  className='home-job-location-icon'
                                >
                                  <path d='M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z' />
                                </svg>
                                {job.address || job.city || "Remote"}
                              </span>
                            </div>

                            {/* Skills */}
                            <div className='home-job-skills'>
                              {job.jobSkills?.map((skill, index) => (
                                <Badge key={index} className='home-skill-badge'>
                                  {skill}
                                </Badge>
                              ))}
                              <Button
                                className='custom-button apply-button home-detail-btn'
                                onClick={() =>
                                  navigate(`/job-detail/${job.id}`)
                                }
                              >
                                Chi tiết
                              </Button>
                            </div>
                          </div>
                        </Card.Body>
                      </Card>
                    </Col>
                  ))}
                </Row>
              ) : (
                <div className='home-empty-state'>
                  <p>No jobs found matching your criteria.</p>
                  <Button
                    className='home-clear-filter-btn'
                    onClick={handleReset}
                  >
                    Clear filters
                  </Button>
                </div>
              )}

              {/* Pagination */}
              {jobs.length > 0 && (
                <div className='home-pagination-wrapper'>
                  <Pagination>
                    <Pagination.Prev
                      onClick={() => setCurrentPage(currentPage - 1)}
                      disabled={currentPage === 1}
                    />
                    {renderPagination()}
                    <Pagination.Next
                      onClick={() => setCurrentPage(currentPage + 1)}
                      disabled={currentPage === totalPages}
                    />
                  </Pagination>
                </div>
              )}
            </Container>
          </div>
        </div>
        <ChatbotCareerRecommend />
      </Container>
    </>
  );
};

export default Home;
