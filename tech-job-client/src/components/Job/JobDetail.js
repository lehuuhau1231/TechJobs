import React, { useContext, useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Container,
  Row,
  Col,
  Button,
  Card,
  Badge,
  Spinner,
  Modal,
  Form,
  Alert,
} from "react-bootstrap";
import {
  MapPin,
  Calendar,
  Clock,
  Building,
  Star,
  Heart,
  Upload,
  AlertCircle,
} from "lucide-react";
import Apis, { endpoints, authApis } from "../../configs/Apis";
import "../styles/jobDetail.css";
import Header from "../layout/Header";
import "../styles/common.css";
import Loading from "../layout/Loading";
import { MyUserContext } from "../Context/MyContext";
import cookies from "react-cookies";
import AlertSuccess from "../layout/AlertSuccess";

const JobDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isFavorite, setIsFavorite] = useState(false);
  const [user] = useContext(MyUserContext);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [message, setMessage] = useState("");
  const [cv, setCv] = useState(null);
  const [cvName, setCvName] = useState("");
  const [applyLoading, setApplyLoading] = useState(false);
  const [applyError, setApplyError] = useState("");
  const [applySuccess, setApplySuccess] = useState(false);
  const [alertSuccess, setAlertSuccess] = useState(false);
  const [isApplied, setIsApplied] = useState(false);
  const fileInputRef = useRef(null);

  useEffect(() => {
    const fetchJobDetail = async () => {
      try {
        setLoading(true);
        const response = await Apis.get(`${endpoints.job}/${id}`);
        setJob(response.data);
        setLoading(false);
      } catch (err) {
        setError("Không thể tải thông tin công việc. Vui lòng thử lại sau.");
        setLoading(false);
        console.error("Error fetching job details:", err);
      }
    };

    fetchJobDetail();
    checkApplied();
  }, []);

  const checkApplied = async () => {
    const token = cookies.load("token");
    if (token) {
      try {
        const response = await authApis(token).get(
          `${endpoints.check_applied}/${id}`,
        );
        setIsApplied(response.data.hasApplied);
      } catch (error) {
        console.error("Error checking application status:", error);
      }
    }
  };

  const toggleFavorite = () => {
    setIsFavorite(!isFavorite);
    // Gọi API để lưu/xóa công việc yêu thích
  };

  const handleApplyClick = () => {
    if (!user) {
      setShowLoginModal(true);
    } else {
      // Reset các giá trị và hiển thị modal ứng tuyển
      setMessage("");
      setCv(null);
      setCvName("");
      setApplyError("");
      setApplySuccess(false);
      setShowApplyModal(true);
    }
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      // Kiểm tra định dạng file
      if (selectedFile.type !== "application/pdf") {
        setApplyError("Vui lòng chọn file PDF");
        setCv(null);
        setCvName("");
        return;
      }
      // Kiểm tra kích thước file (tối đa 5MB)
      if (selectedFile.size > 5 * 1024 * 1024) {
        setApplyError("Kích thước file không được vượt quá 5MB");
        setCv(null);
        setCvName("");
        return;
      }
      setCv(selectedFile);
      setCvName(selectedFile.name);
      setApplyError("");
    }
  };

  const handleApplySubmit = async () => {
    // Kiểm tra dữ liệu
    if (!cv) {
      setApplyError("Vui lòng tải lên CV của bạn");
      return;
    }

    setApplyLoading(true);
    setApplyError("");

    try {
      const formData = new FormData();
      formData.append("job", id);
      formData.append("message", message);
      formData.append("cv", cv);

      const token = cookies.load("token");
      console.log("Applying with token:", token);
      const res = await authApis(token).post(endpoints.application, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      if (res.status === 201 || res.status === 200) {
        setApplySuccess(true);
        setShowApplyModal(false);
        setAlertSuccess(true);
      }
    } catch (err) {
      console.error("Lỗi khi gửi đơn ứng tuyển:", err);
      setApplyError(
        err.response?.data?.message ||
          "Có lỗi xảy ra khi gửi đơn ứng tuyển. Vui lòng thử lại sau.",
      );
    } finally {
      setApplyLoading(false);
    }
  };

  const redirectToLogin = () => {
    navigate(`/login?next=/job-detail/${id}`);
  };

  const handleUpdateClick = () => {
    window.open(`/create-job?editJobId=${job.id}`, '_blank');
  };

  const formatSalary = (min, max) => {
    if (min && max) {
      return `${min.toLocaleString()} - ${max.toLocaleString()}`;
    } else if (min) {
      return `Từ ${min.toLocaleString()}`;
    } else if (max) {
      return `Đến ${max.toLocaleString()}`;
    }
    return "Thương lượng";
  };

  const formatWorkingHours = (startTime, endTime) => {
    if (startTime && endTime) {
      return `${startTime.substring(0, 5)} - ${endTime.substring(0, 5)}`;
    }
    return "Linh hoạt";
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      setAlertSuccess(false);
    }, 3000);
    return () => clearTimeout(timer);
  }, [alertSuccess]);

  if (error) {
    return (
      <Container className='mt-4'>
        <div className='alert alert-danger'>{error}</div>
      </Container>
    );
  }

  if (!job) {
    return null;
  }

  let errors = {};
  if (job.fieldErrors) {
    try {
      errors = JSON.parse(job.fieldErrors);
    } catch (e) {
      console.error("Failed to parse fieldErrors", e);
    }
  }

  return (
    <div className="job-detail-wrapper">
      <Header />
      <Container className='mt-5'>
        {loading && <Loading />}
        {alertSuccess && (
          <AlertSuccess message='Đã gửi đơn ứng tuyển thành công!' />
        )}

        {/* Cảnh báo tin tuyển dụng bị từ chối */}
        {job.status === 'REJECTED' && (
          <Alert variant="danger" className="mb-4 p-4 border-0 rounded-4 shadow-sm" style={{ backgroundColor: '#fef2f2', color: '#991b1b', borderLeft: '5px solid #dc2626 !important' }}>
            <div className="d-flex align-items-start gap-3">
              <AlertCircle size={28} className="text-danger mt-1 flex-shrink-0" />
              <div>
                <h4 className="alert-heading fw-bold mb-2" style={{ color: '#991b1b' }}>Tin tuyển dụng bị từ chối tự động</h4>
                <p className="mb-0 text-muted" style={{ fontSize: '0.95rem' }}>
                  Bài viết của bạn đã bị từ chối do không đáp ứng các tiêu chuẩn kiểm duyệt tuyển dụng. 
                  Vui lòng xem chi tiết lỗi tại từng khu vực bên dưới, điều chỉnh lại nội dung tuyển dụng và cập nhật lại.
                </p>
                {job.rejectReason && (
                  <div className="mt-3 p-3 rounded-3 text-danger small" style={{ backgroundColor: '#fee2e2', border: '1px solid #fca5a5' }}>
                    <strong>Lý do chung:</strong> {job.rejectReason}
                  </div>
                )}
              </div>
            </div>
          </Alert>
        )}
        
        {/* Hero Section */}
        <div className='job-hero-card'>
          <Row className="align-items-center">
            <Col lg={9}>
              <div className='d-flex align-items-start gap-4'>
                <div className='company-logo-wrapper'>
                  <img
                    src={job.avatar}
                    alt={job.companyName}
                    style={{ width: "80px", height: "80px", objectFit: "contain" }}
                  />
                </div>
                <div className="flex-grow-1">
                  <h1 className='job-title'>{job.title}</h1>
                  {errors.title && (
                    <div className="text-danger small mt-2 d-flex align-items-center gap-2 px-3 py-2 rounded-3 border" style={{ backgroundColor: '#fee2e2', borderColor: '#fca5a5' }}>
                      <AlertCircle size={16} className="flex-shrink-0" />
                      <strong>Lỗi Tiêu đề:</strong> {errors.title}
                    </div>
                  )}
                  <div className='company-name mb-3'>
                    <Building size={20} />
                    {job.companyName}
                  </div>
                  <div className="d-flex flex-wrap gap-4 mt-4">
                    <div className='job-meta-item'>
                      <MapPin size={18} className="text-primary" />
                      {job.city}
                    </div>
                    <div className='job-meta-item'>
                      <Calendar size={18} className="text-primary" />
                      Hạn nộp: {new Date(job.endDate).toLocaleDateString("vi-VN")}
                    </div>
                    <div className='job-meta-item'>
                      <Clock size={18} className="text-primary" />
                      {job.jobTypeName}
                    </div>
                  </div>
                </div>
              </div>
            </Col>
            <Col lg={3} className="mt-4 mt-lg-0">
              <div className="d-flex flex-column gap-3">
                {job.status === "APPROVED" && (
                  <button
                  className='apply-button-main'
                  onClick={handleApplyClick}
                  disabled={job.status !== "APPROVED" || isApplied}
                >
                  {isApplied ? "ĐÃ ỨNG TUYỂN" : "ỨNG TUYỂN NGAY"}
                </button>
                )}
                
                {job.status === "APPROVED" && (
                <button
                  className='favorite-button-outline d-flex align-items-center justify-content-center gap-2'
                  onClick={toggleFavorite}
                >
                  <Heart
                    size={20}
                    fill={isFavorite ? "var(--primary-color)" : "none"}
                    stroke={isFavorite ? "var(--primary-color)" : "currentColor"}
                  />
                  {isFavorite ? "ĐÃ LƯU" : "LƯU CÔNG VIỆC"}
                </button>
                )}

                {job.status === "REJECTED" && (
                  <button
                    className='apply-button-main w-100 d-flex align-items-center justify-content-center gap-2'
                    onClick={handleUpdateClick}
                    style={{ backgroundColor: '#dc2626', borderColor: '#dc2626', color: '#ffffff' }}
                  >
                    CẬP NHẬT CÔNG VIỆC
                  </button>
                )}
              </div>
            </Col>
          </Row>
        </div>

        <Row>
          <Col lg={8}>
            {/* Skills & Working Hours */}
            <div className="d-flex flex-wrap gap-2 mb-5">
              {job.jobSkills && job.jobSkills.map((skill, index) => (
                <span key={index} className="job-badge-pill">{skill}</span>
              ))}
              <span className="job-badge-pill">
                {formatWorkingHours(job.startTime, job.endTime)}
              </span>
            </div>

            {/* Content Sections */}
            <div className="job-section-card">
              <div className="section-header">Yêu cầu kinh nghiệm</div>
              <div className="section-body">
                <Row>
                  <Col md={6}>
                    <div className="mb-3">
                      <span className="text-muted d-block small text-uppercase fw-bold mb-1">Cấp bậc</span>
                      <span className="fw-bold text-dark">{job.jobLevelName}</span>
                    </div>
                  </Col>
                  <Col md={6}>
                    <div className="mb-3">
                      <span className="text-muted d-block small text-uppercase fw-bold mb-1">Hình thức</span>
                      <span className="fw-bold text-dark">{job.jobTypeName}</span>
                    </div>
                  </Col>
                </Row>
              </div>
            </div>

             <div className={`job-section-card ${errors.description ? 'border border-danger border-2 shadow-sm' : ''}`} style={errors.description ? { borderColor: '#dc2626' } : {}}>
              <div className="section-header d-flex justify-content-between align-items-center">
                <span>Mô tả công việc</span>
                {errors.description && <Badge bg="danger" className="rounded-pill px-3 py-2">Cần chỉnh sửa</Badge>}
              </div>
              <div className="section-body">
                {errors.description && (
                  <Alert variant="danger" className="py-2 px-3 mb-3 border-0 small rounded-3 d-flex align-items-center gap-2" style={{ backgroundColor: '#fee2e2', color: '#991b1b' }}>
                    <AlertCircle size={16} className="text-danger flex-shrink-0" />
                    <span><strong>Lỗi kiểm duyệt:</strong> {errors.description}</span>
                  </Alert>
                )}
                <div
                  className='job-content-html'
                  dangerouslySetInnerHTML={{ __html: job.description }}
                />
              </div>
             </div>

             <div className={`job-section-card ${errors.jobRequire ? 'border border-danger border-2 shadow-sm' : ''}`} style={errors.jobRequire ? { borderColor: '#dc2626' } : {}}>
              <div className="section-header d-flex justify-content-between align-items-center">
                <span>Kỹ năng và kinh nghiệm</span>
                {errors.jobRequire && <Badge bg="danger" className="rounded-pill px-3 py-2">Cần chỉnh sửa</Badge>}
              </div>
              <div className="section-body">
                {errors.jobRequire && (
                  <Alert variant="danger" className="py-2 px-3 mb-3 border-0 small rounded-3 d-flex align-items-center gap-2" style={{ backgroundColor: '#fee2e2', color: '#991b1b' }}>
                    <AlertCircle size={16} className="text-danger flex-shrink-0" />
                    <span><strong>Lỗi kiểm duyệt:</strong> {errors.jobRequire}</span>
                  </Alert>
                )}
                <div
                  className='job-content-html'
                  dangerouslySetInnerHTML={{ __html: job.jobRequire }}
                />
              </div>
             </div>

             <div className={`job-section-card ${errors.benefits ? 'border border-danger border-2 shadow-sm' : ''}`} style={errors.benefits ? { borderColor: '#dc2626' } : {}}>
              <div className="section-header d-flex justify-content-between align-items-center">
                <span>Quyền lợi dành cho bạn</span>
                {errors.benefits && <Badge bg="danger" className="rounded-pill px-3 py-2">Cần chỉnh sửa</Badge>}
              </div>
              <div className="section-body">
                {errors.benefits && (
                  <Alert variant="danger" className="py-2 px-3 mb-3 border-0 small rounded-3 d-flex align-items-center gap-2" style={{ backgroundColor: '#fee2e2', color: '#991b1b' }}>
                    <AlertCircle size={16} className="text-danger flex-shrink-0" />
                    <span><strong>Lỗi kiểm duyệt:</strong> {errors.benefits}</span>
                  </Alert>
                )}
                <div
                  className='job-content-html'
                  dangerouslySetInnerHTML={{ __html: job.benefits }}
                />
              </div>
             </div>
          </Col>

          {/* Sticky Sidebar */}
          <Col lg={4}>
            <div className="floating-sidebar">
              <h5 className="fw-bold mb-4 text-uppercase" style={{ letterSpacing: '0.05em' }}>Tóm tắt công việc</h5>
              <div className="summary-item">
                <span className="summary-label">Mức lương</span>
                <span className="summary-value" style={{ color: 'var(--primary-color)' }}>{formatSalary(job.salaryMin, job.salaryMax)}</span>
              </div>
              <div className="summary-item">
                <span className="summary-label">Cấp bậc</span>
                <span className="summary-value">{job.jobLevelName}</span>
              </div>
              <div className="summary-item">
                <span className="summary-label">Địa điểm</span>
                <span className="summary-value">{job.cityName}</span>
              </div>
              <div className="summary-item">
                <span className="summary-label">Kinh nghiệm</span>
                <span className="summary-value">{job.experienceName || "Không yêu cầu"}</span>
              </div>
              <div className="summary-item">
                <span className="summary-label">Hợp đồng</span>
                <span className="summary-value">{job.contractTypeName}</span>
              </div>
              <div className="summary-item" style={{ borderBottom: 'none', marginBottom: 0 }}>
                <span className="summary-label">Ngày hết hạn</span>
                <span className="summary-value">{new Date(job.endDate).toLocaleDateString("vi-VN")}</span>
              </div>
              
              {job.status === "APPROVED" && (
              <div className="mt-4 pt-4 border-top">
                <button
                  className='apply-button-main mb-3'
                  onClick={handleApplyClick}
                  disabled={job.status !== "APPROVED" || isApplied}
                >
                  {isApplied ? "ĐÃ ỨNG TUYỂN" : "ỨNG TUYỂN NGAY"}
                </button>
                <p className="text-center small text-muted">
                  Ứng tuyển nhanh chóng chỉ với 1 click
                </p>
              </div>
              )}

              {job.status === "REJECTED" && (
              <div className="mt-4 pt-4 border-top">
                <button
                  className='apply-button-main mb-3'
                  onClick={handleUpdateClick}
                  style={{ backgroundColor: '#dc2626', borderColor: '#dc2626', color: '#ffffff' }}
                >
                  CẬP NHẬT CÔNG VIỆC
                </button>
                <p className="text-center small text-muted">
                  Chỉnh sửa và gửi duyệt lại tin tuyển dụng
                </p>
              </div>
              )}
            </div>
          </Col>
        </Row>
      </Container>

      {/* Login Modal */}
      <Modal
        show={showLoginModal}
        onHide={() => setShowLoginModal(false)}
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>Thông báo</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Vui lòng đăng nhập để ứng tuyển công việc này.</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant='secondary' onClick={() => setShowLoginModal(false)}>
            Đóng
          </Button>
          <Button className='button' onClick={redirectToLogin}>
            Đến trang đăng nhập
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Apply Modal */}
      <Modal
        show={showApplyModal}
        onHide={() => !applyLoading && setShowApplyModal(false)}
        centered
        size='lg'
      >
        <Modal.Header closeButton>
          <Modal.Title>Ứng tuyển công việc: {job?.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {applySuccess ? (
            <Alert variant='success'>
              <p className='mb-0'>
                Đơn ứng tuyển của bạn đã được gửi thành công!
              </p>
              <p className='mb-0'>
                Chúng tôi sẽ xem xét và liên hệ với bạn trong thời gian sớm
                nhất.
              </p>
            </Alert>
          ) : (
            <Form>
              {applyError && <Alert variant='danger'>{applyError}</Alert>}

              <Form.Group className='mb-3'>
                <Form.Label>Giới thiệu về bạn (không bắt buộc)</Form.Label>
                <Form.Control
                  as='textarea'
                  rows={4}
                  placeholder='Hãy chia sẻ một chút về bạn và lý do bạn muốn ứng tuyển vị trí này...'
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                />
              </Form.Group>

              <Form.Group className='mb-3'>
                <Form.Label>
                  Tải lên CV của bạn (PDF, tối đa 5MB){" "}
                  <span className='text-danger'>*</span>
                </Form.Label>
                <div className='d-flex align-items-center'>
                  <input
                    type='file'
                    ref={fileInputRef}
                    accept='.pdf'
                    onChange={handleFileChange}
                    style={{ display: "none" }}
                  />
                  <Button
                    variant='outline-secondary'
                    onClick={() => fileInputRef.current.click()}
                    className='me-2'
                  >
                    <Upload size={16} className='me-2' />
                    Chọn file
                  </Button>
                  <span>{cvName || "Chưa chọn file nào"}</span>
                </div>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button
            variant='secondary'
            onClick={() => setShowApplyModal(false)}
            disabled={applyLoading}
          >
            Đóng
          </Button>
          {!applySuccess && (
            <Button
              className='button'
              onClick={handleApplySubmit}
              disabled={applyLoading}
            >
              {applyLoading ? <Loading /> : "Gửi đơn ứng tuyển"}
            </Button>
          )}
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default JobDetail;
