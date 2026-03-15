import React, { useEffect, useState, useCallback } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Image,
  Badge,
} from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import Loading from "../layout/Loading";
import Header from "../layout/Header";
import { format } from "date-fns";
import {
  ArrowLeft,
  Calendar,
  Mail,
  Phone,
  MapPin,
  User,
  FileText,
} from "lucide-react";
import cookies from "react-cookies";

const CandidateDetail = () => {
  const { candidateId, applicationId } = useParams();
  const [candidateDetail, setCandidateDetail] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const loadCandidateDetail = async () => {
    setLoading(true);
    try {
      const token = cookies.load("token");
      const res = await authApis(token).get(
        `${endpoints.candidate_applied_detail}/${candidateId}/${applicationId}`
      );
      setCandidateDetail(res.data);
    } catch (ex) {
      console.error("Lỗi khi tải thông tin ứng viên:", ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (candidateId && applicationId) {
      loadCandidateDetail();
    }
  }, []);

  const formatDate = (dateString) => {
    if (!dateString) return "";
    return format(new Date(dateString), "dd/MM/yyyy");
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return "";
    return format(new Date(dateString), "dd/MM/yyyy HH:mm");
  };

  const goBack = () => {
    navigate(-1);
  };

  const openCV = (url) => {
    window.open(url, "_blank");
  };

  const calculateAge = (birthDate) => {
    const today = new Date();
    const birth = new Date(birthDate);
    let age = today.getFullYear() - birth.getFullYear();

    return age;
  };

  if (loading) return <Loading />;

  return (
    <>
      <Header />
      <Container className='my-4'>
        <div className='d-flex justify-content-between align-items-center mb-4'>
          <Button
            variant='outline-secondary'
            onClick={goBack}
            className='d-flex align-items-center'
          >
            <ArrowLeft size={16} className='me-2' />
            Quay lại
          </Button>
        </div>

        {candidateDetail && (
          <Row>
            {/* Left Column - Profile Info */}
            <Col md={4}>
              <Card className='mb-4 shadow-sm'>
                <Card.Body className='text-center'>
                  <div className='mb-4'>
                    <Image
                      src={
                        candidateDetail.avatar ||
                        "https://via.placeholder.com/150"
                      }
                      alt={candidateDetail.fullName}
                      roundedCircle
                      className='img-thumbnail'
                      style={{
                        width: "150px",
                        height: "150px",
                        objectFit: "cover",
                      }}
                    />
                  </div>
                  <h4 className='mb-2'>{candidateDetail.fullName}</h4>
                  <p className='text-muted'>
                    ID: {candidateDetail.candidateId}
                  </p>

                  <div className='text-start'>
                    <div className='d-flex align-items-center mb-2'>
                      <Mail size={18} className='text-primary me-2' />
                      <span>{candidateDetail.email}</span>
                    </div>
                    <div className='d-flex align-items-center mb-2'>
                      <Phone size={18} className='text-primary me-2' />
                      <span>{candidateDetail.phone}</span>
                    </div>
                    <div className='d-flex align-items-center mb-2'>
                      <MapPin size={18} className='text-primary me-2' />
                      <span>
                        {[
                          candidateDetail.address,
                          candidateDetail.district,
                          candidateDetail.city,
                        ]
                          .filter(Boolean)
                          .join(", ")}
                      </span>
                    </div>
                    <div className='d-flex align-items-center'>
                      <Calendar size={18} className='text-primary me-2' />
                      <span>
                        {formatDate(candidateDetail.birthDate)}(
                        {calculateAge(candidateDetail.birthDate)} tuổi)
                      </span>
                    </div>
                  </div>
                </Card.Body>
              </Card>

              {/* CV Section */}
              <Card className='shadow-sm'>
                <Card.Body>
                  <h5 className='d-flex align-items-center mb-3'>
                    <FileText size={20} className='text-primary me-2' />
                    CV ứng tuyển
                  </h5>
                  {candidateDetail.applicationCv ? (
                    <div>
                      <p className='text-muted small mb-3'>
                        CV được nộp kèm đơn ứng tuyển
                      </p>
                      <Button
                        variant='primary'
                        className='w-100 d-flex align-items-center justify-content-center'
                        onClick={() => openCV(candidateDetail.applicationCv)}
                      >
                        Xem CV
                      </Button>
                    </div>
                  ) : (
                    <p className='text-muted'>Ứng viên chưa nộp CV</p>
                  )}
                </Card.Body>
              </Card>
            </Col>

            {/* Right Column - Detailed Info */}
            <Col md={8}>
              {/* Application Info */}
              <Card className='mb-4 shadow-sm'>
                <Card.Header>
                  <h5 className='mb-0'>Thông tin ứng tuyển</h5>
                </Card.Header>
                <Card.Body>
                  <Row>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Ngày ứng tuyển</h6>
                        <p className='fw-medium'>
                          <Calendar size={16} className='me-2 text-primary' />
                          {formatDateTime(candidateDetail.appliedDate)}
                        </p>
                      </div>
                    </Col>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>ID đơn ứng tuyển</h6>
                        <p className='fw-medium'>
                          #{candidateDetail.applicationId}
                        </p>
                      </div>
                    </Col>
                  </Row>

                  <div className='mb-3'>
                    <h6 className='text-muted'>Thư xin việc</h6>
                    <Card className='bg-light border-0'>
                      <Card.Body>
                        <p className='mb-0'>
                          {candidateDetail.message ||
                            "Ứng viên chưa để lại thông điệp"}
                        </p>
                      </Card.Body>
                    </Card>
                  </div>
                </Card.Body>
              </Card>

              {/* Personal Information */}
              <Card className='mb-4 shadow-sm'>
                <Card.Header>
                  <h5 className='mb-0 d-flex align-items-center'>
                    <User size={18} className='text-primary me-2' />
                    Thông tin cá nhân
                  </h5>
                </Card.Header>
                <Card.Body>
                  <div className='mb-4'>
                    <h6 className='text-muted'>Giới thiệu bản thân</h6>
                    <Card className='bg-light border-0'>
                      <Card.Body>
                        <p className='mb-0'>
                          {candidateDetail.selfDescription ||
                            "Ứng viên chưa có thông tin giới thiệu bản thân."}
                        </p>
                      </Card.Body>
                    </Card>
                  </div>

                  <Row>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Họ và tên</h6>
                        <p className='fw-medium'>{candidateDetail.fullName}</p>
                      </div>
                    </Col>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Email</h6>
                        <p className='fw-medium'>{candidateDetail.email}</p>
                      </div>
                    </Col>
                  </Row>

                  <Row>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Số điện thoại</h6>
                        <p className='fw-medium'>{candidateDetail.phone}</p>
                      </div>
                    </Col>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Ngày sinh</h6>
                        <p className='fw-medium'>
                          {formatDate(candidateDetail.birthDate)}
                          <Badge bg='secondary' className='ms-2'>
                            {calculateAge(candidateDetail.birthDate)} tuổi
                          </Badge>
                        </p>
                      </div>
                    </Col>
                  </Row>

                  <div className='mb-3'>
                    <h6 className='text-muted'>Địa chỉ</h6>
                    <p className='fw-medium'>
                      {[
                        candidateDetail.address,
                        candidateDetail.district,
                        candidateDetail.city,
                      ]
                        .filter(Boolean)
                        .join(", ")}
                    </p>
                  </div>
                </Card.Body>
              </Card>

              {/* Action Buttons */}
              <Card className='shadow-sm'>
                <Card.Body>
                  <div className='d-flex justify-content-end gap-2'>
                    <Button variant='success' size='lg'>
                      Chấp nhận ứng viên
                    </Button>
                    <Button variant='danger' size='lg'>
                      Từ chối ứng viên
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        )}
      </Container>
    </>
  );
};

export default CandidateDetail;
