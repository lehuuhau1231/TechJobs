import React from "react";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Header from "../layout/Header";
import "../styles/common.css";
import { User, Briefcase } from "lucide-react";

const RegisterSwitch = () => {
  const navigate = useNavigate();

  return (
    <>
      <Header />
      <div className='min-vh-100 d-flex align-items-center justify-content-center bg-light py-5'>
        <Container>
          <Row className='justify-content-center'>
            <Col lg={10}>
              <Card className='border-0 shadow-sm'>
                <Card.Body className='p-4 p-md-5'>
                  {/* Logo */}
                  <div className='text-center mb-4'>
                    <div className='d-flex align-items-center justify-content-center mb-3'>
                      <div
                        className='me-2'
                        style={{
                          fontSize: "24px",
                          fontWeight: "bold",
                          color: "#4285f4",
                        }}
                      >
                        &lt;/&gt;
                      </div>
                      <span
                        style={{
                          fontSize: "24px",
                          fontWeight: "bold",
                          color: "#4285f4",
                        }}
                      >
                        Tech Job
                      </span>
                    </div>
                  </div>

                  {/* Welcome Text */}
                  <div className='text-center mb-5'>
                    <h4 className='fw-bold mb-2'>Chọn loại tài khoản</h4>
                    <p className='text-muted mb-0'>
                      Bạn muốn đăng ký với vai trò gì?
                    </p>
                    <div className='mt-2'>
                      <span className='text-muted me-1'>Đã có tài khoản?</span>
                      <a
                        href='/login'
                        className='text-decoration-none'
                        style={{ color: "#4285f4" }}
                        onClick={(e) => {
                          e.preventDefault();
                          navigate("/login");
                        }}
                      >
                        Đăng nhập
                      </a>
                    </div>
                  </div>

                  {/* Option Cards */}
                  <Row className='g-4'>
                    {/* Candidate Option */}
                    <Col md={6}>
                      <Card
                        className='h-100 border-0 shadow-sm hover-shadow transition-all'
                        onClick={() => navigate("/candidate-register")}
                        style={{ cursor: "pointer" }}
                      >
                        <Card.Body className='p-4 text-center'>
                          <div
                            className='rounded-circle bg-light d-flex align-items-center justify-content-center mx-auto mb-4'
                            style={{ width: "80px", height: "80px" }}
                          >
                            <User size={40} className='text-primary' />
                          </div>
                          <h5 className='fw-bold mb-3'>Ứng viên tìm việc</h5>
                          <p className='text-muted mb-4'>
                            Tạo hồ sơ, ứng tuyển vào các vị trí công việc phù
                            hợp với kỹ năng và kinh nghiệm của bạn.
                          </p>
                          <Button
                            variant='outline-primary'
                            className='px-4 py-2'
                            onClick={(e) => {
                              e.stopPropagation();
                              navigate("/candidate-register");
                            }}
                          >
                            Đăng ký ứng viên
                          </Button>
                        </Card.Body>
                      </Card>
                    </Col>

                    {/* Employer Option */}
                    <Col md={6}>
                      <Card
                        className='h-100 border-0 shadow-sm hover-shadow transition-all'
                        onClick={() => navigate("/employer-register")}
                        style={{ cursor: "pointer" }}
                      >
                        <Card.Body className='p-4 text-center'>
                          <div
                            className='rounded-circle bg-light d-flex align-items-center justify-content-center mx-auto mb-4'
                            style={{ width: "80px", height: "80px" }}
                          >
                            <Briefcase size={40} className='text-primary' />
                          </div>
                          <h5 className='fw-bold mb-3'>Nhà tuyển dụng</h5>
                          <p className='text-muted mb-4'>
                            Đăng tin tuyển dụng, tìm kiếm ứng viên phù hợp và
                            quản lý quá trình tuyển dụng.
                          </p>
                          <Button
                            variant='outline-primary'
                            className='px-4 py-2'
                            onClick={(e) => {
                              e.stopPropagation();
                              navigate("/employer-register");
                            }}
                          >
                            Đăng ký nhà tuyển dụng
                          </Button>
                        </Card.Body>
                      </Card>
                    </Col>
                  </Row>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>

      {/* Add custom CSS for hover effect */}
      <style jsx='true'>{`
        .hover-shadow:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
        }
        .transition-all {
          transition: all 0.3s ease;
        }
      `}</style>
    </>
  );
};

export default RegisterSwitch;
