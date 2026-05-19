import React, { useEffect, useState, useCallback } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Image,
  Badge,
  Spinner,
  Nav,
  Tab,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import Loading from "../layout/Loading";
import { format } from "date-fns";
import {
  Calendar,
  MessageSquare,
  User,
  UserCheck,
  Phone,
  Mail,
  MapPin,
  Check,
  Clock,
  Eye,
} from "lucide-react";
import Header from "../layout/Header";
import cookies from "react-cookies";
import AlertSuccess from "../layout/AlertSuccess";

const CandidateApply = () => {
  const { jobId } = useParams();
  const navigate = useNavigate();

  const [application, setApplication] = useState([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const token = cookies.load("token");
  const [message, setMessage] = useState("");
  const [alertSuccess, setAlertSuccess] = useState(false);
  const [acceptLoading, setAcceptLoading] = useState(false);
  const [cancelLoading, setCancelLoading] = useState(false);
  const [activeTab, setActiveTab] = useState("pending");
  const [contactCandidates, setContactCandidates] = useState([]);
  const [contactLoading, setContactLoading] = useState({});

  const loadApplication = async () => {
    setLoading(true);
    try {
      const res = await authApis(token).get(
        `${endpoints.application_pending}?page=${currentPage}&jobId=${jobId}`,
      );
      setApplication(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (ex) {
      console.error("Lỗi khi tải danh sách ứng viên:", ex);
    } finally {
      setLoading(false);
    }
  };

  const loadContactCandidates = async () => {
    try {
      const res = await authApis(token).get(
        `${endpoints.application_approve_candidate}/${jobId}`,
      );
      setContactCandidates(res.data);
    } catch (ex) {
      console.error("Lỗi khi tải danh sách ứng viên cần liên hệ:", ex);
    }
  };

  const handleContactUpdate = async (applicationId) => {
    setContactLoading((prev) => ({ ...prev, [applicationId]: true }));
    try {
      await authApis(token).patch(endpoints.application_contacted, {
        applicationId: applicationId,
      });

      // Cập nhật lại danh sách sau khi đánh dấu đã liên hệ
      setContactCandidates((prev) =>
        prev.map((candidate) =>
          candidate.applicationId === applicationId
            ? { ...candidate, contacted: true }
            : candidate,
        ),
      );
      setAlertSuccess(true);
      setMessage("Đã đánh dấu liên hệ thành công.");
    } catch (ex) {
      console.error("Lỗi khi cập nhật trạng thái liên hệ:", ex);
    } finally {
      setContactLoading((prev) => ({ ...prev, [applicationId]: false }));
    }
  };

  useEffect(() => {
    if (activeTab === "pending") {
      loadApplication();
    } else if (activeTab === "contact") {
      loadContactCandidates();
    }
  }, [currentPage, activeTab]);

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
      loadApplication();
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
      loadApplication();
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
          <h2>Quản lý ứng viên</h2>
        </div>

        <Nav variant='tabs' className='mb-4'>
          <Nav.Item>
            <Nav.Link
              active={activeTab === "pending"}
              onClick={() => setActiveTab("pending")}
              className='d-flex align-items-center'
            >
              <User className='me-2' size={16} />
              Ứng viên chờ duyệt ({application.length})
            </Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link
              active={activeTab === "contact"}
              onClick={() => setActiveTab("contact")}
              className='d-flex align-items-center'
            >
              <UserCheck className='me-2' size={16} />
              Ứng viên cần liên hệ ({contactCandidates.length})
            </Nav.Link>
          </Nav.Item>
        </Nav>

        {alertSuccess && <AlertSuccess message={message} />}

        <Tab.Content>
          <Tab.Pane active={activeTab === "pending"}>
            {loading ? (
              <Loading />
            ) : (
              <>
                {application.length > 0 ? (
                  <>
                    <Row xs={1} md={1} className='g-4'>
                      {application.map((application, index) => (
                        <Col key={index}>
                          <Card className='border-0 shadow-sm'>
                            <Card.Body>
                              <Row>
                                <Col md={2} className='text-center'>
                                  <Image
                                    src={application.candidateAvatar}
                                    alt={application.candidateFullName}
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
                                    <h5>{application.candidateFullName}</h5>
                                    <Badge
                                      bg='info'
                                      className='d-flex align-items-center'
                                    >
                                      <Calendar size={14} className='me-1' />
                                      {formatDate(application.appliedDate)}
                                    </Badge>
                                  </div>
                                  <Card.Text className='mt-3'>
                                    <MessageSquare
                                      size={16}
                                      className='me-2 text-muted'
                                    />
                                    <strong>Thông điệp:</strong>{" "}
                                    {application.message}
                                  </Card.Text>
                                  <div className='mt-3 d-flex justify-content-end'>
                                    <Button
                                      variant='outline-primary'
                                      className='me-2'
                                      onClick={() =>
                                        navigate(
                                          `/employer/candidate-detail/${application.candidateId}/${application.id}`,
                                        )
                                      }
                                    >
                                      Xem hồ sơ
                                    </Button>
                                    <Button
                                      variant='success'
                                      className='me-2'
                                      onClick={() =>
                                        handleAccept(application.id)
                                      }
                                    >
                                      Chấp nhận
                                      {acceptLoading && (
                                        <Spinner animation='border' size='sm' />
                                      )}
                                    </Button>
                                    <Button
                                      variant='danger'
                                      onClick={() =>
                                        handleReject(application.id)
                                      }
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
          </Tab.Pane>

          <Tab.Pane active={activeTab === "contact"}>
            {contactCandidates.length > 0 ? (
              <Row>
                {contactCandidates.map((candidate) => (
                  <Col
                    md={6}
                    lg={4}
                    key={candidate.applicationId}
                    className='mb-3'
                  >
                    <Card className='h-100 border-success'>
                      <Card.Body>
                        <div className='d-flex align-items-center mb-3'>
                          <Image
                            src={
                              candidate.avatar ||
                              "https://via.placeholder.com/60"
                            }
                            alt={candidate.fullName}
                            roundedCircle
                            className='me-3'
                            style={{
                              width: "60px",
                              height: "60px",
                              objectFit: "cover",
                            }}
                          />
                          <div className='flex-grow-1'>
                            <h6 className='mb-1 fw-bold'>
                              {candidate.fullName}
                            </h6>
                            <small className='text-muted'>
                              {candidate.email}
                            </small>
                          </div>
                        </div>

                        <div className='mb-2'>
                          <div className='d-flex align-items-center mb-1'>
                            <Phone className='me-2 text-muted' size={14} />
                            <small>{candidate.phone}</small>
                          </div>
                          <div className='d-flex align-items-center'>
                            <MapPin className='me-2 text-muted' size={14} />
                            <small>
                              {[
                                candidate.address,
                                candidate.district,
                                candidate.city,
                              ]
                                .filter(Boolean)
                                .join(", ")}
                            </small>
                          </div>
                        </div>

                        <div className='mb-3'>
                          {candidate.contacted ? (
                            <Badge bg='success' className='p-2'>
                              <Check className='me-1' size={14} />
                              Đã liên hệ
                            </Badge>
                          ) : (
                            <Badge bg='warning' className='p-2'>
                              <Clock className='me-1' size={14} />
                              Chưa liên hệ
                            </Badge>
                          )}
                        </div>

                        <div className='d-grid gap-2'>
                          {candidate.cv && (
                            <Button
                              variant='outline-info'
                              size='sm'
                              onClick={() =>
                                window.open(candidate.cv, "_blank")
                              }
                            >
                              <Eye className='me-1' size={14} />
                              Xem CV
                            </Button>
                          )}

                          <div className='d-flex gap-2'>
                            <Button
                              variant='outline-primary'
                              size='sm'
                              href={`mailto:${candidate.email}`}
                              className='flex-fill'
                            >
                              <Mail className='me-1' size={14} />
                              Email
                            </Button>
                            <Button
                              variant='outline-success'
                              size='sm'
                              href={`tel:${candidate.phone}`}
                              className='flex-fill'
                            >
                              <Phone className='me-1' size={14} />
                              Gọi
                            </Button>
                          </div>

                          {!candidate.contacted && (
                            <Button
                              variant='success'
                              size='sm'
                              disabled={contactLoading[candidate.applicationId]}
                              onClick={() =>
                                handleContactUpdate(candidate.applicationId)
                              }
                            >
                              {contactLoading[candidate.applicationId] ? (
                                <Spinner size='sm' className='me-1' />
                              ) : (
                                <Check className='me-1' size={14} />
                              )}
                              Đánh dấu đã liên hệ
                            </Button>
                          )}
                        </div>
                      </Card.Body>
                    </Card>
                  </Col>
                ))}
              </Row>
            ) : (
              <Card className='text-center p-5 bg-light'>
                <Card.Body>
                  <UserCheck size={48} className='text-muted mb-3' />
                  <h5>Chưa có ứng viên nào được duyệt</h5>
                  <p className='text-muted'>
                    Hiện tại chưa có ứng viên nào được duyệt cho công việc này.
                  </p>
                </Card.Body>
              </Card>
            )}
          </Tab.Pane>
        </Tab.Content>
      </Container>
    </>
  );
};

export default CandidateApply;
