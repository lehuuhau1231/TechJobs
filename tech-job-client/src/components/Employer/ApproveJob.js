import React, { useContext, useEffect, useState } from "react";
import { Container, Card, Button, Badge, Table } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import Loading from "../layout/Loading";
import { Link, useNavigate } from "react-router-dom";
import { Calendar, Users, Eye } from "lucide-react";
import { format } from "date-fns";
import cookies from "react-cookies";
import Header from "../layout/Header";

const ApproveJob = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadJobs();
  }, []);

  const loadJobs = async () => {
    setLoading(true);
    try {
      const token = cookies.load("token");
      const res = await authApis(token).get(endpoints.approve_job);
      setJobs(res.data);
    } catch (ex) {
      console.error("Error loading jobs:", ex);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    return format(new Date(dateString), "MMM dd, yyyy");
  };

  const viewCandidates = (jobId) => {
    navigate(`/employer/job/${jobId}/candidates`);
  };

  return (
    <>
      <Header />
      <Container className='my-4'>
        <h2 className='mb-4'>Quản lý công việc</h2>

        {loading ? (
          <Loading />
        ) : (
          <>
            {jobs.length > 0 ? (
              <Table striped bordered hover responsive>
                <thead>
                  <tr className='bg-light'>
                    <th>STT</th>
                    <th>Tên công việc</th>
                    <th>Ngày đăng</th>
                    <th>Số lượng ứng tuyển</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  {jobs.map((job, index) => (
                    <tr key={job.id}>
                      <td>{index + 1}</td>
                      <td>
                        <Link
                          to={`/jobs/${job.id}`}
                          className='text-decoration-none'
                        >
                          {job.title}
                        </Link>
                      </td>
                      <td>
                        <Calendar size={16} className='me-2 text-muted' />
                        {formatDate(job.postedDate)}
                      </td>
                      <td>
                        <Badge
                          bg={
                            job.applicationCount > 0 ? "success" : "secondary"
                          }
                          pill
                        >
                          <Users size={14} className='me-1' />
                          {job.applicationCount}
                        </Badge>
                      </td>
                      <td>
                        <Button
                          variant='outline-primary'
                          size='sm'
                          onClick={() => viewCandidates(job.id)}
                          disabled={job.applicationCount === 0}
                        >
                          <Eye size={14} className='me-1' />
                          Xem ứng viên
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <Card className='text-center p-5 bg-light'>
                <Card.Body>
                  <h5>Không có công việc</h5>
                  <p className='text-muted'>
                    Bạn chưa có công việc nào được phê duyệt.
                  </p>
                  <Button as={Link} to='/employer/create-job' variant='primary'>
                    Đăng một công việc mới
                  </Button>
                </Card.Body>
              </Card>
            )}
          </>
        )}
      </Container>
    </>
  );
};

export default ApproveJob;
