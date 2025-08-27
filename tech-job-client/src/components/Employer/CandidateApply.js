import React, { useContext, useEffect, useState, useCallback } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Pagination,
  Image,
  Badge,
  Spinner,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../Context/MyContext";
import Loading from "../layout/Loading";
import { format, set } from "date-fns";
import { Calendar, MessageSquare, ArrowLeft } from "lucide-react";
import Header from "../layout/Header";
import cookies from "react-cookies";
import AlertSuccess from "../layout/AlertSuccess";
import { id } from "date-fns/locale";

const CandidateApply = () => {
  const { jobId } = useParams();

  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const token = cookies.load("token");
  const [message, setMessage] = useState("");
  const [alertSuccess, setAlertSuccess] = useState(false);
  const [acceptLoading, setAcceptLoading] = useState(false);
  const [cancelLoading, setCancelLoading] = useState(false);

  const loadCandidates = async () => {
    setLoading(true);
    try {
      const res = await authApis(token).get(
        `${endpoints.application_pending}?page=${currentPage}&jobId=${jobId}`
      );
      setCandidates(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (ex) {
      console.error("Lỗi khi tải danh sách ứng viên:", ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCandidates();
  }, [currentPage]);

  const formatDate = (dateString) => {
    return format(new Date(dateString), "dd/MM/yyyy HH:mm");
  };

  const handleAccept = async (candidateId) => {
    try {
      setAcceptLoading(true);
      await authApis(token).post(`${endpoints.application_status}`, {
        id: candidateId,
        status: "APPROVED",
      });
      loadCandidates();
      setAlertSuccess(true);
      setMessage("Đã chấp nhận ứng viên thành công.");
    } catch (ex) {
      console.error("Lỗi khi chấp nhận ứng viên:", ex);
    } finally {
      setAcceptLoading(false);
    }
  };

  const handleReject = async (candidateId) => {
    try {
      setCancelLoading(true);
      await authApis(token).post(`${endpoints.application_status}`, {
        id: candidateId,
        status: "CANCELED",
      });
      loadCandidates();
      setAlertSuccess(true);
      setMessage("Đã từ chối ứng viên thành công.");
    } catch (ex) {
      console.error("Lỗi khi từ chối ứng viên:", ex);
    } finally {
      setCancelLoading(false);
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      setAlertSuccess(false);
    }, 3000);
    return () => clearTimeout(timer);
  }, [alertSuccess]);

  return (
    <>
      <Header />
      <Container className='my-4'>
        <div className='d-flex justify-content-between align-items-center mb-4'>
          <h2>Danh sách ứng viên ({totalPages})</h2>
        </div>
        {alertSuccess && <AlertSuccess message={message} />}
        {loading ? (
          <Loading />
        ) : (
          <>
            {candidates.length > 0 ? (
              <>
                <Row xs={1} md={1} className='g-4'>
                  {candidates.map((candidate) => (
                    <Col key={candidate.id}>
                      <Card className='border-0 shadow-sm'>
                        <Card.Body>
                          <Row>
                            <Col md={2} className='text-center'>
                              <Image
                                src={candidate.candidateAvatar}
                                alt={candidate.candidateFullName}
                                roundedCircle
                                className='mb-2'
                                style={{
                                  width: "80px",
                                  height: "80px",
                                  objectFit: "cover",
                                }}
                              />
                            </Col>
                            <Col md={10}>
                              <div className='d-flex justify-content-between align-items-start'>
                                <h5>{candidate.candidateFullName}</h5>
                                <Badge
                                  bg='info'
                                  className='d-flex align-items-center'
                                >
                                  <Calendar size={14} className='me-1' />
                                  {formatDate(candidate.appliedDate)}
                                </Badge>
                              </div>
                              <Card.Text className='mt-3'>
                                <MessageSquare
                                  size={16}
                                  className='me-2 text-muted'
                                />
                                <strong>Thông điệp:</strong> {candidate.message}
                              </Card.Text>
                              <div className='mt-3 d-flex justify-content-end'>
                                <Button
                                  variant='outline-primary'
                                  className='me-2'
                                >
                                  Xem hồ sơ
                                </Button>
                                <Button
                                  variant='success'
                                  className='me-2'
                                  onClick={() => handleAccept(candidate.id)}
                                >
                                  Chấp nhận
                                  {acceptLoading && (
                                    <Spinner animation='border' size='sm' />
                                  )}
                                </Button>
                                <Button
                                  variant='danger'
                                  onClick={() => handleReject(candidate.id)}
                                >
                                  Từ chối
                                  {cancelLoading && (
                                    <Spinner animation='border' size='sm' />
                                  )}
                                </Button>
                              </div>
                            </Col>
                          </Row>
                        </Card.Body>
                      </Card>
                    </Col>
                  ))}
                </Row>

                {totalPages > 1 && (
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
              </>
            ) : (
              <Card className='text-center p-5 bg-light'>
                <Card.Body>
                  <h5>Không có ứng viên nào</h5>
                  <p className='text-muted'>
                    Hiện tại chưa có ứng viên nào đăng ký vào vị trí này.
                  </p>
                </Card.Body>
              </Card>
            )}
          </>
        )}
      </Container>
    </>
  );
};

export default CandidateApply;
