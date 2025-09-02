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
  }, [id]);

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
          "Có lỗi xảy ra khi gửi đơn ứng tuyển. Vui lòng thử lại sau."
      );
    } finally {
      setApplyLoading(false);
    }
  };

  const redirectToLogin = () => {
    navigate(`/login?next=/job-detail/${id}`);
  };

  const formatSalary = (min, max) => {
    if (min && max) {
      return `$${min.toLocaleString()} - $${max.toLocaleString()}`;
    } else if (min) {
      return `Từ $${min.toLocaleString()}`;
    } else if (max) {
      return `Đến $${max.toLocaleString()}`;
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

  return (
    <>
      <Header />
      <Container className='my-4'>
        {loading && <Loading />}
        {alertSuccess && (
          <AlertSuccess message='Đã gửi đơn ứng tuyển thành công!' />
        )}
        <Card className='mb-4 job-detail-header'>
          <Card.Body>
            <Row>
              <Col md={8}>
                <div className='d-flex'>
                  <div className='company-logo me-3'>
                    <img
                      src={job.avatar}
                      alt={job.companyName}
                      className='img-fluid'
                      style={{ maxWidth: "80px", maxHeight: "80px" }}
                    />
                  </div>
                  <div>
                    <h2 className='job-title'>{job.title}</h2>
                    <div className='company-name mb-2'>
                      <Building size={16} className='me-2 text-muted' />
                      {job.companyName}
                    </div>
                    <div className='location mb-2'>
                      <MapPin size={16} className='me-2' />
                      {job.address}, {job.district}, {job.city}
                    </div>
                    <div className='posted-date'>
                      <Calendar size={16} className='me-2 text-muted' />
                      {`Thời hạn: ${new Date(job.startDate).toLocaleDateString(
                        "vi-VN"
                      )} - ${new Date(job.endDate).toLocaleDateString(
                        "vi-VN"
                      )}`}
                    </div>
                  </div>
                </div>
              </Col>
              <Col
                md={4}
                className='d-flex flex-column justify-content-center align-items-end'
              >
                <Button
                  className='apply-btn mb-2 w-100 button'
                  onClick={handleApplyClick}
                  disabled={job.status !== "APPROVED"}
                >
                  Ứng tuyển ngay
                </Button>
                <Button
                  variant='outline'
                  style={{ borderColor: "#4f46e5", color: "#4f46e5" }}
                  className='favorite-btn w-100'
                  onClick={toggleFavorite}
                >
                  <Heart
                    size={16}
                    className={isFavorite ? "primary-color" : ""}
                  />{" "}
                  {isFavorite ? "Đã lưu" : "Lưu công việc"}
                </Button>
              </Col>
            </Row>
          </Card.Body>
        </Card>

        {/* Job details */}
        <Row>
          <Col lg={8}>
            {/* Job tags */}
            <div className='mb-4'>
              <span style={{ fontWeight: "bold", marginRight: "10px" }}>
                Kỹ năng:
              </span>
              {job.jobSkills &&
                job.jobSkills.map((skill, index) => (
                  <Badge
                    key={index}
                    bg='light'
                    text='dark'
                    pill
                    className='job-badge me-2 mb-2'
                  >
                    {skill}
                  </Badge>
                ))}
            </div>
            <div className='mb-4'>
              <span style={{ fontWeight: "bold", marginRight: "10px" }}>
                Thời gian làm việc:
              </span>
              <Badge
                bg='light'
                text='dark'
                pill
                className='job-badge me-2 mb-2'
              >
                {formatWorkingHours(job.startTime, job.endTime)}
              </Badge>
            </div>

            {/* Job Experience */}
            <Card className='mb-4'>
              <Card.Header as='h5'>Yêu cầu kinh nghiệm</Card.Header>
              <Card.Body>
                <Row>
                  <Col md={6}>
                    <div className='mb-3'>
                      <strong>Cấp bậc:</strong> {job.jobLevelName}
                    </div>
                  </Col>
                  <Col md={6}>
                    <div className='mb-3'>
                      <strong>Hình thức làm việc:</strong> {job.jobTypeName}
                    </div>
                  </Col>
                </Row>
              </Card.Body>
            </Card>

            {/* Top 3 reasons */}
            <Card className='mb-4'>
              <Card.Header as='h5'>
                Top 3 lý do để gia nhập chúng tôi
              </Card.Header>
              <Card.Body>
                <ul className='top-reasons'>
                  <li>
                    <Star size={16} className='text-warning me-2' />
                    Mức lương hấp dẫn (
                    {formatSalary(job.salaryMin, job.salaryMax)})
                  </li>
                  <li>
                    <Star size={16} className='text-warning me-2' />
                    Văn hóa công ty chuyên nghiệp
                  </li>
                  <li>
                    <Star size={16} className='text-warning me-2' />
                    Cơ hội thăng tiến
                  </li>
                </ul>
              </Card.Body>
            </Card>

            {/* Job description */}
            <Card className='mb-4'>
              <Card.Header as='h5'>Mô tả công việc</Card.Header>
              <Card.Body>
                <div
                  className='job-description'
                  dangerouslySetInnerHTML={{ __html: job.description }}
                />
              </Card.Body>
            </Card>

            {/* Your skills and experience */}
            <Card className='mb-4'>
              <Card.Header as='h5'>Kỹ năng và kinh nghiệm</Card.Header>
              <Card.Body>
                <div
                  className='job-requirements'
                  dangerouslySetInnerHTML={{ __html: job.jobRequire }}
                />
              </Card.Body>
            </Card>

            {/* Why you'll love working here */}
            <Card className='mb-4'>
              <Card.Header as='h5'>
                Tại sao bạn sẽ thích làm việc ở đây
              </Card.Header>
              <Card.Body>
                <div
                  className='job-benefits'
                  dangerouslySetInnerHTML={{ __html: job.benefits }}
                />
              </Card.Body>
            </Card>
          </Col>

          {/* Right sidebar */}
          <Col lg={4}>
            <Card className='mb-4 sticky-top sticky-sidebar'>
              <Card.Header as='h5'>Tóm tắt công việc</Card.Header>
              <Card.Body>
                <div className='mb-3'>
                  <strong>Công ty:</strong> {job.companyName}
                </div>
                <div className='mb-3'>
                  <strong>Địa điểm:</strong> {job.cityName}, {job.district}
                </div>
                <div className='mb-3'>
                  <strong>Cấp bậc:</strong> {job.jobLevelName}
                </div>
                <div className='mb-3'>
                  <strong>Hình thức:</strong> {job.contractTypeName}
                </div>
                <div className='mb-3'>
                  <strong>Làm việc:</strong> {job.jobTypeName}
                </div>
                <div className='mb-3'>
                  <strong>Lương:</strong>{" "}
                  {formatSalary(job.salaryMin, job.salaryMax)}
                </div>
                <div className='mb-3'>
                  <strong>Giờ làm việc:</strong>{" "}
                  {formatWorkingHours(job.startTime, job.endTime)}
                </div>
                <div className='mb-3'>
                  <strong>Thời hạn nộp:</strong>{" "}
                  {new Date(job.endDate).toLocaleDateString("vi-VN")}
                </div>
                <Button
                  className='apply-btn w-100 mt-2 button'
                  onClick={handleApplyClick}
                  disabled={job.status !== "APPROVED"}
                >
                  Ứng tuyển ngay
                </Button>
                <Button
                  variant='outline'
                  style={{ borderColor: "#4f46e5", color: "#4f46e5" }}
                  className='favorite-btn w-100 mt-2'
                  onClick={toggleFavorite}
                >
                  <Heart
                    size={16}
                    className={isFavorite ? "primary-color" : ""}
                  />{" "}
                  {isFavorite ? "Đã lưu" : "Lưu công việc"}
                </Button>
              </Card.Body>
            </Card>
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
    </>
  );
};

export default JobDetail;
