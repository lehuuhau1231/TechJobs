import React, { useState, useEffect, useCallback } from "react";
import {
  Container,
  Row,
  Col,
  Form,
  Button,
  Card,
  Alert,
  InputGroup,
} from "react-bootstrap";
import "quill/dist/quill.snow.css";
import cookies from "react-cookies";
import "../styles/common.css";
import AlertSuccess from "../layout/AlertSuccess";
import { useNavigate } from "react-router-dom";
import { useQuill } from "react-quilljs";
import "quill/dist/quill.snow.css";
import Select from "react-select";
import { Clock, DollarSign, MapPin } from "lucide-react";
import Apis, { endpoints, authApis } from "../../configs/Apis";
import Header from "../layout/Header";
import Loading from "../layout/Loading";

import "../styles/common.css";

const CreateJob = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [alertSuccess, setAlertSuccess] = useState(false);
  const [amount] = useState(100000); // Giá cố định là 100.000 VND

  // Form data
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    address: "",
    ageFrom: 18,
    ageTo: 60,
    startDate: new Date().toISOString().split("T")[0], // Format: YYYY-MM-DD
    endDate: new Date(new Date().setMonth(new Date().getMonth() + 1))
      .toISOString()
      .split("T")[0],
    startTime: "09:00",
    endTime: "18:00",
    salaryMin: 0,
    salaryMax: 0,
    jobRequire: "",
    benefits: "",
    cityId: null,
    districtId: null,
    jobLevelId: null,
    jobTypeId: null,
    contractTypeId: null,
    jobSkillIds: [],
  });

  // Options for dropdowns
  const [cities, setCities] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [jobLevels, setJobLevels] = useState([]);
  const [jobTypes, setJobTypes] = useState([]);
  const [contractTypes, setContractTypes] = useState([]);
  const [skills, setSkills] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(true);
  const [token] = useState(cookies.load("token"));

  // Load all options data when component mounts
  useEffect(() => {
    fetchOptionsData();
  }, []);

  const fetchOptionsData = async () => {
    setLoadingOptions(true);
    try {
      const [
        citiesRes,
        jobLevelsRes,
        jobTypesRes,
        contractTypesRes,
        skillsRes,
      ] = await Promise.all([
        Apis.get(endpoints.cities),
        Apis.get(endpoints.job_levels),
        Apis.get(endpoints.job_types),
        Apis.get(endpoints.contract_types),
        Apis.get(endpoints.skills),
      ]);

      setCities(citiesRes.data);
      setJobLevels(jobLevelsRes.data);
      setJobTypes(jobTypesRes.data);
      setContractTypes(contractTypesRes.data);
      setSkills(skillsRes.data);
    } catch (err) {
      console.error("Error loading options:", err);
      setError("Không thể tải dữ liệu. Vui lòng thử lại sau.");
    } finally {
      setLoadingOptions(false);
    }
  };

  // Load districts when city is selected
  useEffect(() => {
    if (formData.cityId) {
      const fetchDistricts = async () => {
        try {
          const res = await Apis.get(
            `${endpoints.districts}/${formData.cityId}`
          );
          setDistricts(res.data);
        } catch (err) {
          console.error("Error loading districts:", err);
        }
      };
      fetchDistricts();
    } else {
      setDistricts([]);
      setFormData((prev) => ({ ...prev, districtId: null }));
    }
  }, [formData.cityId]);

  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Handle date changes
  const handleDateChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Initialize quill editor settings
  const quillModules = {
    toolbar: [
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      ["bold", "italic", "underline", "strike"],
      [{ list: "ordered" }, { list: "bullet" }],
      ["link"],
    ],
  };

  const quillFormats = [
    "header",
    "bold",
    "italic",
    "underline",
    "strike",
    "list",
    "bullet",
    "link",
  ];

  // Editor instances for different fields
  const { quill: descriptionQuill, quillRef: descriptionQuillRef } = useQuill({
    modules: quillModules,
    formats: quillFormats,
    placeholder: "Mô tả chi tiết về công việc, trách nhiệm, yêu cầu...",
  });

  const { quill: jobRequireQuill, quillRef: jobRequireQuillRef } = useQuill({
    modules: quillModules,
    formats: quillFormats,
    placeholder: "Các yêu cầu về kinh nghiệm, kỹ năng, học vấn...",
  });

  const { quill: benefitsQuill, quillRef: benefitsQuillRef } = useQuill({
    modules: quillModules,
    formats: quillFormats,
    placeholder:
      "Các quyền lợi khi làm việc tại công ty: lương thưởng, phúc lợi...",
  });

  // Update form data when editors change
  useEffect(() => {
    if (descriptionQuill) {
      // Register handler only once
      const handler = () => {
        setFormData((prev) => ({
          ...prev,
          description: descriptionQuill.root.innerHTML,
        }));
      };

      descriptionQuill.on("text-change", handler);

      // Cleanup
      return () => {
        descriptionQuill.off("text-change", handler);
      };
    }
  }, [descriptionQuill]);

  // Set initial content separately to avoid dependency cycles
  useEffect(() => {
    if (
      descriptionQuill &&
      formData.description &&
      !descriptionQuill.root.innerHTML
    ) {
      descriptionQuill.clipboard.dangerouslyPasteHTML(formData.description);
    }
  }, [descriptionQuill, formData.description]);

  useEffect(() => {
    if (jobRequireQuill) {
      // Register handler only once
      const handler = () => {
        setFormData((prev) => ({
          ...prev,
          jobRequire: jobRequireQuill.root.innerHTML,
        }));
      };

      jobRequireQuill.on("text-change", handler);

      // Cleanup
      return () => {
        jobRequireQuill.off("text-change", handler);
      };
    }
  }, [jobRequireQuill]);

  // Set initial content separately to avoid dependency cycles
  useEffect(() => {
    if (
      jobRequireQuill &&
      formData.jobRequire &&
      !jobRequireQuill.root.innerHTML
    ) {
      jobRequireQuill.clipboard.dangerouslyPasteHTML(formData.jobRequire);
    }
  }, [jobRequireQuill, formData.jobRequire]);

  useEffect(() => {
    if (benefitsQuill) {
      // Register handler only once
      const handler = () => {
        setFormData((prev) => ({
          ...prev,
          benefits: benefitsQuill.root.innerHTML,
        }));
      };

      benefitsQuill.on("text-change", handler);

      // Cleanup
      return () => {
        benefitsQuill.off("text-change", handler);
      };
    }
  }, [benefitsQuill]);

  // Set initial content separately to avoid dependency cycles
  useEffect(() => {
    if (benefitsQuill && formData.benefits && !benefitsQuill.root.innerHTML) {
      benefitsQuill.clipboard.dangerouslyPasteHTML(formData.benefits);
    }
  }, [benefitsQuill, formData.benefits]);

  // Handle multi-select changes for skills
  const handleMultiSelectChange = (selectedOptions) => {
    const selectedSkills = selectedOptions
      ? selectedOptions.map((option) => option.value)
      : [];
    setFormData((prev) => ({
      ...prev,
      jobSkillIds: selectedSkills,
    }));
  };

  // Verify payment
  const verifyPayment = useCallback(async () => {
    const queryParams = new URLSearchParams(window.location.search);
    const vnpResponseCode = queryParams.get("vnp_ResponseCode");

    if (vnpResponseCode) {
      try {
        setLoading(true);
        const queryString = window.location.search;
        const billId = cookies.load("billId");
        let url = `${endpoints.return_payment}${queryString}&billId=${billId}`;
        console.log("getUrl:", url);
        const response = await authApis(token).get(url);
        console.log("response: ", response);
        if (response.data.status === "success") {
          cookies.remove("billId");
          setAlertSuccess(true);
          window.history.replaceState(
            {},
            document.title,
            window.location.pathname
          );
        }
      } catch (error) {
        console.log(error);
        setError("Có lỗi khi xác minh thanh toán");
      } finally {
        setLoading(false);
      }
    }
  }, [token]);

  const handlePayment = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log("formData:", formData);
      const jobResponse = await authApis(token).post(endpoints.job, formData);

      if (jobResponse.status !== 201 && jobResponse.status !== 200) {
        setError("Có lỗi khi tạo công việc. Vui lòng thử lại.");
        setLoading(false);
        return;
      } else {
        const jobId = jobResponse.data.jobId;
        const billResponse = await authApis(token).post(endpoints.bill, {
          jobId: jobId,
          amount: amount,
        });

        if (billResponse.status === 201 || billResponse.status === 200) {
          cookies.save("billId", billResponse.data.id);
          const response = await authApis(token).post(
            endpoints.create_payment,
            {
              amount: billResponse.data.amount,
              billId: billResponse.data.id,
            }
          );
          console.log("paymentUrl:", response.data.paymentUrl);
          window.location.href = response.data.paymentUrl;
        }
      }
    } catch (error) {
      console.log(error);
      setError("Có lỗi khi tạo đơn hàng hoặc thanh toán");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const queryParams = new URLSearchParams(window.location.search);
    if (queryParams.get("vnp_ResponseCode")) {
      verifyPayment();
    }
  }, [verifyPayment]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setAlertSuccess(false);
    }, 3000);
    return () => clearTimeout(timer);
  }, [alertSuccess]);

  if (loadingOptions) {
    return (
      <>
        <Header />
        <Container className='my-5 text-center'>
          <Loading />
          <p>Đang tải dữ liệu...</p>
        </Container>
      </>
    );
  }

  return (
    <>
      <Header />
      <Container className='my-4'>
        <h2 className='mb-4'>Tạo công việc mới</h2>

        {error && <Alert variant='danger'>{error}</Alert>}
        {alertSuccess && (
          <AlertSuccess message='Thanh toán thành công! Vui lòng chờ duyệt' />
        )}

        <Card>
          <Card.Body>
            <Form>
              {/* Basic Info Section */}
              <h4 className='mb-3'>Thông tin cơ bản</h4>
              <Row className='mb-4'>
                <Col md={12} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Tiêu đề công việc <span className='text-danger'>*</span>
                    </Form.Label>
                    <Form.Control
                      type='text'
                      name='title'
                      value={formData.title}
                      onChange={handleInputChange}
                      placeholder='Ví dụ: Thực tập sinh Backend .NET Developer'
                      required
                    />
                  </Form.Group>
                </Col>

                <Col md={12} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Địa chỉ làm việc <span className='text-danger'>*</span>
                    </Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <MapPin size={16} />
                      </InputGroup.Text>
                      <Form.Control
                        type='text'
                        name='address'
                        value={formData.address}
                        onChange={handleInputChange}
                        placeholder='Địa chỉ công ty/văn phòng'
                        required
                      />
                    </InputGroup>
                  </Form.Group>
                </Col>

                <Col md={6}>
                  <Form.Group className='mb-3'>
                    <Form.Label>
                      Thành phố <span className='text-danger'>*</span>
                    </Form.Label>
                    <Form.Select
                      name='cityId'
                      value={formData.cityId || ""}
                      onChange={(e) =>
                        handleInputChange({
                          target: {
                            name: "cityId",
                            value: e.target.value
                              ? Number(e.target.value)
                              : null,
                          },
                        })
                      }
                      required
                    >
                      <option value=''>Chọn thành phố</option>
                      {cities.map((city) => (
                        <option key={city.id} value={city.id}>
                          {city.name}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>

                <Col md={6}>
                  <Form.Group className='mb-3'>
                    <Form.Label>
                      Quận/Huyện <span className='text-danger'>*</span>
                    </Form.Label>
                    <Form.Select
                      name='districtId'
                      value={formData.districtId || ""}
                      onChange={(e) =>
                        handleInputChange({
                          target: {
                            name: "districtId",
                            value: e.target.value
                              ? Number(e.target.value)
                              : null,
                          },
                        })
                      }
                      disabled={!formData.cityId}
                      required
                    >
                      <option value=''>Chọn quận/huyện</option>
                      {districts.map((district) => (
                        <option key={district.id} value={district.id}>
                          {district.name}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>
              </Row>

              {/* Job Classification Section */}
              <h4 className='mb-3'>Phân loại công việc</h4>
              <Row className='mb-4'>
                <Col md={4}>
                  <Form.Group className='mb-3'>
                    <Form.Label>
                      Cấp bậc <span className='text-danger'>*</span>
                    </Form.Label>
                    <Form.Select
                      name='jobLevelId'
                      value={formData.jobLevelId || ""}
                      onChange={(e) =>
                        handleInputChange({
                          target: {
                            name: "jobLevelId",
                            value: e.target.value
                              ? Number(e.target.value)
                              : null,
                          },
                        })
                      }
                      required
                    >
                      <option value=''>Chọn cấp bậc</option>
                      {jobLevels.map((level) => (
                        <option key={level.id} value={level.id}>
                          {level.name}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>

                <Col md={4}>
                  <Form.Group className='mb-3'>
                    <Form.Label>
                      Hình thức làm việc <span className='text-danger'>*</span>
                    </Form.Label>
                    <Form.Select
                      name='jobTypeId'
                      value={formData.jobTypeId || ""}
                      onChange={(e) =>
                        handleInputChange({
                          target: {
                            name: "jobTypeId",
                            value: e.target.value
                              ? Number(e.target.value)
                              : null,
                          },
                        })
                      }
                      required
                    >
                      <option value=''>Chọn hình thức</option>
                      {jobTypes.map((type) => (
                        <option key={type.id} value={type.id}>
                          {type.name}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>

                <Col md={4}>
                  <Form.Group className='mb-3'>
                    <Form.Label>
                      Loại hợp đồng <span className='text-danger'>*</span>
                    </Form.Label>
                    <Form.Select
                      name='contractTypeId'
                      value={formData.contractTypeId || ""}
                      onChange={(e) =>
                        handleInputChange({
                          target: {
                            name: "contractTypeId",
                            value: e.target.value
                              ? Number(e.target.value)
                              : null,
                          },
                        })
                      }
                      required
                    >
                      <option value=''>Chọn loại hợp đồng</option>
                      {contractTypes.map((type) => (
                        <option key={type.id} value={type.id}>
                          {type.name}
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                </Col>

                <Col md={12}>
                  <Form.Group className='mb-3'>
                    <Form.Label>
                      Kỹ năng <span className='text-danger'>*</span>
                    </Form.Label>
                    <Select
                      isMulti
                      name='jobSkillIds'
                      options={skills.map((skill) => ({
                        value: skill.id,
                        label: skill.name,
                      }))}
                      className='basic-multi-select'
                      classNamePrefix='select'
                      placeholder='Tìm kiếm và chọn kỹ năng...'
                      onChange={handleMultiSelectChange}
                      value={formData.jobSkillIds
                        .map((id) => {
                          const skill = skills.find((s) => s.id === id);
                          return skill
                            ? { value: skill.id, label: skill.name }
                            : null;
                        })
                        .filter(Boolean)}
                    />
                    <Form.Text className='text-muted'>
                      Bạn có thể tìm kiếm và chọn nhiều kỹ năng
                    </Form.Text>
                  </Form.Group>
                </Col>
              </Row>

              {/* Job Details Section */}
              <h4 className='mb-3'>Chi tiết công việc</h4>
              <Row className='mb-4'>
                <Col md={12} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Mô tả công việc <span className='text-danger'>*</span>
                    </Form.Label>
                    <div style={{ height: "250px", marginBottom: "30px" }}>
                      <div ref={descriptionQuillRef} />
                    </div>
                    <Form.Text
                      className='text-muted'
                      style={{ marginTop: "15px", display: "block" }}
                    ></Form.Text>
                  </Form.Group>
                </Col>

                <Col md={12} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Yêu cầu công việc <span className='text-danger'>*</span>
                    </Form.Label>
                    <div style={{ height: "250px", marginBottom: "30px" }}>
                      <div ref={jobRequireQuillRef} />
                    </div>
                    <Form.Text
                      className='text-muted'
                      style={{ marginTop: "15px", display: "block" }}
                    ></Form.Text>
                  </Form.Group>
                </Col>

                <Col md={12} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Quyền lợi <span className='text-danger'>*</span>
                    </Form.Label>
                    <div style={{ height: "250px", marginBottom: "30px" }}>
                      <div ref={benefitsQuillRef} />
                    </div>
                    <Form.Text
                      className='text-muted'
                      style={{ marginTop: "15px", display: "block" }}
                    ></Form.Text>
                  </Form.Group>
                </Col>
              </Row>

              {/* Additional Info Section */}
              <h4 className='mb-3'>Thông tin thêm</h4>
              <Row className='mb-4'>
                <Col md={6} className='mb-3'>
                  <Form.Group>
                    <Form.Label>Độ tuổi từ</Form.Label>
                    <Form.Control
                      type='number'
                      name='ageFrom'
                      value={formData.ageFrom}
                      onChange={handleInputChange}
                      min={18}
                      max={60}
                    />
                  </Form.Group>
                </Col>

                <Col md={6} className='mb-3'>
                  <Form.Group>
                    <Form.Label>Đến</Form.Label>
                    <Form.Control
                      type='number'
                      name='ageTo'
                      value={formData.ageTo}
                      onChange={handleInputChange}
                      min={18}
                      max={65}
                    />
                  </Form.Group>
                </Col>

                <Col md={6} className='mb-3'>
                  <Form.Group>
                    <Form.Label>Lương tối thiểu (VNĐ)</Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <DollarSign size={16} />
                      </InputGroup.Text>
                      <Form.Control
                        type='number'
                        name='salaryMin'
                        value={formData.salaryMin}
                        onChange={handleInputChange}
                        min={0}
                      />
                    </InputGroup>
                  </Form.Group>
                </Col>

                <Col md={6} className='mb-3'>
                  <Form.Group>
                    <Form.Label>Lương tối đa (VNĐ)</Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <DollarSign size={16} />
                      </InputGroup.Text>
                      <Form.Control
                        type='number'
                        name='salaryMax'
                        value={formData.salaryMax}
                        onChange={handleInputChange}
                        min={0}
                      />
                    </InputGroup>
                  </Form.Group>
                </Col>

                <Col md={6} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Giờ bắt đầu <span className='text-danger'>*</span>
                    </Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <Clock size={16} />
                      </InputGroup.Text>
                      <Form.Control
                        type='time'
                        name='startTime'
                        value={formData.startTime}
                        onChange={handleInputChange}
                        required
                      />
                    </InputGroup>
                  </Form.Group>
                </Col>

                <Col md={6} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Giờ kết thúc <span className='text-danger'>*</span>
                    </Form.Label>
                    <InputGroup>
                      <InputGroup.Text>
                        <Clock size={16} />
                      </InputGroup.Text>
                      <Form.Control
                        type='time'
                        name='endTime'
                        value={formData.endTime}
                        onChange={handleInputChange}
                        required
                      />
                    </InputGroup>
                  </Form.Group>
                </Col>

                <Col md={12} className='mb-3'>
                  <Form.Group>
                    <Form.Label>
                      Thời gian đăng tin <span className='text-danger'>*</span>
                    </Form.Label>
                    <Row className='mt-2'>
                      <Col md={6} className='mb-3'>
                        <Form.Group>
                          <Form.Label>Ngày bắt đầu</Form.Label>
                          <Form.Control
                            type='date'
                            name='startDate'
                            value={formData.startDate}
                            onChange={handleInputChange}
                            required
                          />
                        </Form.Group>
                      </Col>
                      <Col md={6} className='mb-3'>
                        <Form.Group>
                          <Form.Label>Ngày kết thúc</Form.Label>
                          <Form.Control
                            type='date'
                            name='endDate'
                            value={formData.endDate}
                            onChange={handleInputChange}
                            required
                          />
                        </Form.Group>
                      </Col>
                    </Row>
                    <Form.Text className='text-muted'>
                      Phí đăng tin: 100.000 VNĐ
                    </Form.Text>
                  </Form.Group>
                </Col>
              </Row>

              {/* Submit Button */}
              <div className='d-grid gap-2 mt-4'>
                <Button
                  type='submit'
                  className='button'
                  size='lg'
                  disabled={loading}
                  onClick={handlePayment}
                >
                  {loading ? <Loading /> : `Thanh toán 100.000 VNĐ`}
                </Button>
              </div>
            </Form>
          </Card.Body>
        </Card>
      </Container>
    </>
  );
};

export default CreateJob;
