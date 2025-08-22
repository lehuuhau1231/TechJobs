import React, { useState, useContext } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Form,
  Button,
  InputGroup,
  Alert,
} from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import Header from "../layout/Header";
import "../styles/common.css";
import APIs, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../Context/MyContext";
import cookies from "react-cookies";
import Apis from "../../configs/Apis";
import { useNavigate } from "react-router-dom";
import Loading from "../layout/Loading";
import { Eye, EyeOff, LogIn } from "lucide-react";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [, dispatch] = useContext(MyUserContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage("");

    try {
      const loginData = {
        email: email,
        password: password,
      };

      const res = await Apis.post(endpoints.login, loginData);

      if (res && res.data) {
        cookies.save("token", res.data.token);

        dispatch({
          type: "login",
          payload: res.data,
        });

        navigate("/");
      }
    } catch (error) {
      console.error("Login error:", error);
      setErrorMessage("Email hoặc mật khẩu không chính xác!");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    console.log("Google login clicked");
  };

  return (
    <>
      <Header />
      <div className='min-vh-100 d-flex align-items-center justify-content-center bg-light'>
        <Container>
          <Row className='justify-content-center'>
            <Col md={5} lg={4}>
              <Card className='border-0 shadow-sm'>
                <Card.Body className='p-5'>
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
                  <div className='text-center mb-4'>
                    <h4 className='fw-bold mb-2'>Chào mừng trở lại</h4>
                    <p className='text-muted mb-0'>Đăng nhập để tiếp tục</p>
                    <div className='mt-2'>
                      <span className='text-muted me-1'>
                        Chưa có tài khoản?
                      </span>
                      <a
                        href='/register'
                        className='text-decoration-none'
                        style={{ color: "#4285f4" }}
                        onClick={(e) => {
                          e.preventDefault();
                          navigate("/register");
                        }}
                      >
                        Đăng ký ngay
                      </a>
                    </div>
                  </div>

                  {/* Login Form */}
                  <Form onSubmit={handleSubmit}>
                    {errorMessage && (
                      <Alert variant='danger' className='mb-3'>
                        {errorMessage}
                      </Alert>
                    )}

                    {/* Email Field */}
                    <Form.Group className='mb-3'>
                      <Form.Label className='fw-medium'>
                        Địa chỉ email
                      </Form.Label>
                      <InputGroup>
                        <Form.Control
                          type='email'
                          placeholder='your@email.com'
                          value={email}
                          onChange={(e) => setEmail(e.target.value)}
                          required
                          className='border-start-0 ps-2'
                          style={{ boxShadow: "none" }}
                        />
                      </InputGroup>
                    </Form.Group>

                    {/* Password Field */}
                    <Form.Group className='mb-3'>
                      <Form.Label className='fw-medium'>Mật khẩu</Form.Label>
                      <InputGroup>
                        <Form.Control
                          type={showPassword ? "text" : "password"}
                          placeholder='Nhập mật khẩu của bạn'
                          value={password}
                          onChange={(e) => setPassword(e.target.value)}
                          required
                          style={{ boxShadow: "none" }}
                        />
                        <Button
                          variant='outline-secondary'
                          onClick={() => setShowPassword(!showPassword)}
                          style={{ borderLeft: "none" }}
                        >
                          {showPassword ? <EyeOff /> : <Eye />}
                        </Button>
                      </InputGroup>
                    </Form.Group>

                    {/* Remember Me & Forgot Password */}
                    <div className='d-flex justify-content-between align-items-center mb-4'>
                      <Form.Check
                        type='checkbox'
                        id='rememberMe'
                        label='Ghi nhớ đăng nhập'
                        checked={rememberMe}
                        onChange={(e) => setRememberMe(e.target.checked)}
                        className='text-muted'
                      />
                      <a
                        href='#'
                        className='text-decoration-none'
                        style={{ color: "#4285f4" }}
                      >
                        Quên mật khẩu?
                      </a>
                    </div>

                    {/* Login Button */}
                    <Button
                      type='submit'
                      className='w-100 py-2 mb-4 button'
                      disabled={loading}
                    >
                      {loading ? (
                        <Loading />
                      ) : (
                        <>
                          <LogIn /> Đăng nhập
                        </>
                      )}
                    </Button>

                    {/* Divider */}
                    <div className='text-center mb-3'>
                      <span className='text-muted'>Hoặc tiếp tục với</span>
                    </div>

                    {/* Google Login */}
                    <Button
                      variant='outline-secondary'
                      className='w-100 py-2'
                      onClick={handleGoogleLogin}
                      style={{ borderRadius: "8px" }}
                    >
                      <span className='me-2'>🔍</span>
                      Đăng nhập với Google
                    </Button>
                  </Form>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
    </>
  );
}
