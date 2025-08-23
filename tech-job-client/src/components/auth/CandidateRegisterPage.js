import React, { useState, useEffect, useRef } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Form,
  Button,
  InputGroup,
  Alert,
  Image,
} from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import Header from "../layout/Header";
import "../styles/common.css";
import Apis, { endpoints } from "../../configs/Apis";
import { useNavigate } from "react-router-dom";
import Loading from "../layout/Loading";
import "../styles/common.css";
import { Eye, EyeOff, Upload } from "lucide-react";
import AlertSuccess from "../layout/AlertSuccess";

const CandidateRegisterPage = () => {
  const [formData, setFormData] = useState({
    password: "",
    confirmPassword: "",
    email: "",
    phone: "",
    fullName: "",
    address: "",
    city: "",
    district: "",
    birthDate: "",
  });

  const [avatar, setAvatar] = useState(null);
  const [avatarPreview, setAvatarPreview] = useState(null);
  const fileInputRef = useRef(null);
  const [cities, setCities] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [alertSuccess, setAlertSuccess] = useState(false);

  // Thông tin các trường trong form
  // Thông tin các trường trong form
  const formFields = [
    {
      title: "Email",
      field: "email",
      type: "email",
      required: true,
      placeholder: "Nhập địa chỉ email",
      colSize: 6,
    },
    {
      title: "Họ và tên",
      field: "fullName",
      type: "text",
      required: true,
      placeholder: "Nhập họ và tên",
      colSize: 6,
    },
    {
      title: "Số điện thoại",
      field: "phone",
      type: "text",
      required: true,
      placeholder: "Nhập số điện thoại",
      colSize: 6,
    },
    {
      title: "Ngày sinh",
      field: "birthDate",
      type: "date",
      required: false,
      colSize: 6,
    },
    {
      title: "Mật khẩu",
      field: "password",
      type: "password",
      required: true,
      placeholder: "Nhập mật khẩu",
      colSize: 6,
      showPassword: true,
    },
    {
      title: "Xác nhận mật khẩu",
      field: "confirmPassword",
      type: "password",
      required: true,
      placeholder: "Xác nhận mật khẩu",
      colSize: 6,
      showPassword: true,
    },
  ];

  // Tạo một đối tượng để chứa các trường form trong nhóm city/district riêng biệt
  const locationFields = [
    {
      title: "Thành phố",
      field: "city",
      type: "select",
      colSize: 6,
      options: cities,
      optionValue: "name",
      optionLabel: "name",
      optionDataId: "id",
    },
    {
      title: "Quận/Huyện",
      field: "district",
      type: "select",
      colSize: 6,
      options: districts,
      optionValue: "name",
      optionLabel: "name",
      optionDataId: "id",
      disabled: !formData.city || districts.length === 0,
      helpText: !formData.city && "Vui lòng chọn thành phố trước",
    },
  ];

  const navigate = useNavigate();

  const fetchCities = async () => {
    try {
      const response = await Apis.get(endpoints.cities);
      if (response.data) {
        setCities(response.data);
      }
    } catch (error) {
      console.error("Error fetching cities:", error);
    }
  };

  useEffect(() => {
    fetchCities();
  }, []);

  // Fetch districts cho một thành phố cụ thể
  const fetchDistrictsForCity = async (cityId) => {
    try {
      if (cityId) {
        const response = await Apis.get(`${endpoints.districts}/${cityId}`);
        console.log("Districts for city:", response.data);
        if (response.data) {
          setDistricts(response.data);
        }
      }
    } catch (error) {
      console.error("Error fetching districts:", error);
    }
  };

  const handleChange = async (e) => {
    const { name, value } = e.target;

    if (name === "city") {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
        district: "",
      }));

      if (value) {
        const selectedOption = e.target.options[e.target.selectedIndex];
        const cityId = selectedOption.getAttribute("data-id");
        fetchDistrictsForCity(cityId);
      } else {
        setDistricts([]);
      }
    } else {
      setFormData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }

    // Clear error for this field when user types
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: "",
      });
    }
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Validate file type
      const fileTypes = ["image/jpeg", "image/png", "image/jpg", "image/gif"];
      if (!fileTypes.includes(file.type)) {
        setErrors({
          ...errors,
          avatar: "Chỉ chấp nhận file hình ảnh (JPEG, PNG, JPG, GIF)",
        });
        return;
      }

      // Validate file size (max 2MB)
      if (file.size > 2 * 1024 * 1024) {
        setErrors({
          ...errors,
          avatar: "Kích thước file tối đa là 2MB",
        });
        return;
      }

      setAvatar(file);
      setErrors({
        ...errors,
        avatar: "",
      });

      // Create preview URL
      const reader = new FileReader();
      reader.onloadend = () => {
        setAvatarPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const triggerFileInput = () => {
    fileInputRef.current.click();
  };

  const validateForm = () => {
    const newErrors = {};

    // Password validation
    if (!formData.password) {
      newErrors.password = "Mật khẩu là bắt buộc";
    } else if (formData.password.length < 6) {
      newErrors.password = "Mật khẩu phải có ít nhất 6 ký tự";
    }

    // Confirm password validation
    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Mật khẩu xác nhận không khớp";
    }

    // Email validation
    if (!formData.email) {
      newErrors.email = "Email là bắt buộc";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Email không hợp lệ";
    }

    // Phone validation
    if (!formData.phone) {
      newErrors.phone = "Số điện thoại là bắt buộc";
    } else if (!/^(\+84|0)\d{9,10}$/.test(formData.phone)) {
      newErrors.phone =
        "Số điện thoại không hợp lệ (phải có dạng +84 hoặc 0 và 9-10 số)";
    }

    // Full name validation
    if (!formData.fullName) {
      newErrors.fullName = "Họ tên là bắt buộc";
    }

    // Birth date validation
    if (formData.birthDate) {
      const today = new Date();
      const birthDate = new Date(formData.birthDate);
      if (birthDate >= today) {
        newErrors.birthDate = "Ngày sinh phải trong quá khứ";
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setErrorMessage("");
    setSuccessMessage("");

    try {
      // Format birth date if provided (to ISO format yyyy-MM-dd)
      let formattedData = { ...formData };
      delete formattedData.confirmPassword; // Remove confirmPassword as it's not needed for API

      const formDataToSend = new FormData();

      // Add all text fields
      Object.keys(formattedData).forEach((key) => {
        if (formattedData[key]) {
          formDataToSend.append(key, formattedData[key]);
        }
      });

      // Add avatar file if selected
      if (avatar) {
        formDataToSend.append("avatar", avatar);
      }

      const response = await Apis.post(
        endpoints.candidate_register,
        formDataToSend,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      if (response.status === 201 || response.status === 200) {
        setSuccessMessage(
          "Đăng ký thành công! Bạn sẽ được chuyển đến trang đăng nhập."
        );
        // Reset form
        setFormData({
          password: "",
          confirmPassword: "",
          email: "",
          phone: "",
          fullName: "",
          address: "",
          city: "",
          district: "",
          birthDate: "",
        });
        setAvatar(null);
        setAvatarPreview(null);
        setAlertSuccess(true);
        navigate("/login");
      }
    } catch (error) {
      console.error("Registration error:", error);
      if (error.response && error.response.data) {
        if (error.response.data.message) {
          setErrorMessage(error.response.data.message);
        }
      } else {
        setErrorMessage("Đăng ký thất bại. Vui lòng thử lại sau.");
      }
    } finally {
      setLoading(false);
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
      <div className='min-vh-100 d-flex align-items-center justify-content-center bg-light py-5'>
        <Container>
          {alertSuccess && <AlertSuccess message='Đăng ký thành công!' />}
          <Row className='justify-content-center'>
            <Col md={8} lg={7}>
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
                  <div className='text-center mb-4'>
                    <h4 className='fw-bold mb-2'>Đăng ký tài khoản ứng viên</h4>
                    <p className='text-muted mb-0'>
                      Tạo tài khoản để tìm kiếm việc làm
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

                  {/* Alert Messages */}
                  {successMessage && (
                    <Alert variant='success' className='mb-4'>
                      {successMessage}
                    </Alert>
                  )}

                  {errorMessage && (
                    <Alert variant='danger' className='mb-4'>
                      {errorMessage}
                    </Alert>
                  )}

                  {/* Registration Form */}
                  <Form onSubmit={handleSubmit}>
                    <Row>
                      {/* Các trường form động từ mảng formFields */}
                      {formFields.map((field) => (
                        <Col md={field.colSize} key={field.field}>
                          <Form.Group className='mb-3'>
                            <Form.Label className='fw-medium'>
                              {field.title}
                              {field.required && (
                                <span className='text-danger'>*</span>
                              )}
                            </Form.Label>
                            {field.showPassword ? (
                              // Input cho các trường mật khẩu
                              <InputGroup>
                                <Form.Control
                                  type={
                                    field.field === "password"
                                      ? showPassword
                                        ? "text"
                                        : "password"
                                      : showConfirmPassword
                                      ? "text"
                                      : "password"
                                  }
                                  name={field.field}
                                  value={formData[field.field]}
                                  onChange={handleChange}
                                  placeholder={field.placeholder}
                                  isInvalid={!!errors[field.field]}
                                />
                                <Button
                                  variant='outline-secondary'
                                  onClick={() =>
                                    field.field === "password"
                                      ? setShowPassword(!showPassword)
                                      : setShowConfirmPassword(
                                          !showConfirmPassword
                                        )
                                  }
                                >
                                  {field.field === "password" ? (
                                    showPassword ? (
                                      <EyeOff />
                                    ) : (
                                      <Eye />
                                    )
                                  ) : showConfirmPassword ? (
                                    <EyeOff />
                                  ) : (
                                    <Eye />
                                  )}
                                </Button>
                                <Form.Control.Feedback type='invalid'>
                                  {errors[field.field]}
                                </Form.Control.Feedback>
                              </InputGroup>
                            ) : (
                              // Input cho các trường thông thường
                              <>
                                <Form.Control
                                  type={field.type}
                                  name={field.field}
                                  value={formData[field.field]}
                                  onChange={handleChange}
                                  placeholder={field.placeholder}
                                  isInvalid={!!errors[field.field]}
                                />
                                <Form.Control.Feedback type='invalid'>
                                  {errors[field.field]}
                                </Form.Control.Feedback>
                              </>
                            )}
                          </Form.Group>
                        </Col>
                      ))}

                      {/* City và District Fields */}
                      {locationFields.map((field) => (
                        <Col md={field.colSize} key={field.field}>
                          <Form.Group className='mb-3'>
                            <Form.Label className='fw-medium'>
                              {field.title}
                            </Form.Label>
                            <Form.Select
                              name={field.field}
                              value={formData[field.field]}
                              onChange={handleChange}
                              disabled={field.disabled}
                            >
                              <option value=''>
                                Chọn {field.title.toLowerCase()}
                              </option>
                              {field.options.map((option) => (
                                <option
                                  key={option.id}
                                  value={option[field.optionValue]}
                                  data-id={option[field.optionDataId]}
                                >
                                  {option[field.optionLabel]}
                                </option>
                              ))}
                            </Form.Select>
                            {field.helpText && (
                              <Form.Text className='text-muted'>
                                {field.helpText}
                              </Form.Text>
                            )}
                          </Form.Group>
                        </Col>
                      ))}

                      <Form.Group className='mb-3'>
                        <Form.Label className='fw-medium'>Địa chỉ</Form.Label>
                        <Form.Control
                          type='text'
                          name='address'
                          value={formData.address}
                          onChange={handleChange}
                          isInvalid={!!errors.address}
                          placeholder='Địa chỉ'
                        />
                        <Form.Control.Feedback type='invalid'>
                          {errors.address}
                        </Form.Control.Feedback>
                      </Form.Group>

                      {/* Avatar Field */}
                      <Col md={12}>
                        <Form.Group className='mb-4'>
                          <Form.Label className='fw-medium'>Avatar</Form.Label>
                          <div className='d-flex align-items-center'>
                            {avatarPreview && (
                              <div className='me-3'>
                                <Image
                                  src={avatarPreview}
                                  alt='Avatar Preview'
                                  style={{
                                    width: "100px",
                                    height: "100px",
                                    objectFit: "cover",
                                    borderRadius: "50%",
                                  }}
                                />
                              </div>
                            )}
                            <div className='d-flex flex-column flex-grow-1'>
                              <div className='input-group'>
                                <input
                                  type='file'
                                  ref={fileInputRef}
                                  className='form-control'
                                  accept='image/*'
                                  onChange={handleAvatarChange}
                                  style={{ display: "none" }}
                                />
                                <Button
                                  variant='outline-secondary'
                                  onClick={triggerFileInput}
                                  className='d-flex align-items-center'
                                >
                                  <Upload size={16} className='me-2' />
                                  Chọn ảnh
                                </Button>
                                {avatar && (
                                  <div className='ms-2 d-flex align-items-center'>
                                    <span className='text-muted'>
                                      {avatar.name}
                                    </span>
                                    <Button
                                      variant='link'
                                      className='text-danger p-0 ms-2'
                                      onClick={() => {
                                        setAvatar(null);
                                        setAvatarPreview(null);
                                        fileInputRef.current.value = "";
                                      }}
                                    >
                                      ✕
                                    </Button>
                                  </div>
                                )}
                              </div>
                              {errors.avatar && (
                                <div className='text-danger mt-1 small'>
                                  {errors.avatar}
                                </div>
                              )}
                              <small className='text-muted mt-1'>
                                Cho phép PNG, JPG hoặc GIF. Kích thước tối đa
                                2MB.
                              </small>
                            </div>
                          </div>
                        </Form.Group>
                      </Col>
                    </Row>

                    {/* Register Button */}
                    <Button
                      type='submit'
                      className='w-100 py-2 mb-4 button'
                      disabled={loading}
                    >
                      {loading ? <Loading /> : "Đăng ký"}
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
};

export default CandidateRegisterPage;
