import React, { useState, useEffect, useCallback } from "react";
import { Row, Col, Card, Form, Button, Spinner } from "react-bootstrap";
import { 
  TrendingUp, 
  Users, 
  DollarSign, 
  Activity,
  Calendar,
  Filter
} from "lucide-react";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { Bar, Line } from 'react-chartjs-2';
import AdminLayout from "./AdminLayout";
import { authApis, endpoints } from "../../configs/Apis";
import cookies from "react-cookies";
import { motion } from "framer-motion";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

const AdminDashboard = () => {
  const [loading, setLoading] = useState(true);
  const [statType, setStatType] = useState("month");
  const [year, setYear] = useState(new Date().getFullYear());
  const [fromYear, setFromYear] = useState(new Date().getFullYear() - 5);
  const [toYear, setToYear] = useState(new Date().getFullYear());
  const [stats, setStats] = useState([]);
  const [summary, setSummary] = useState({ totalRevenue: 0, totalTransactions: 0 });

  const fetchStats = useCallback(async () => {
    setLoading(true);
    try {
      const token = cookies.load("token");
      const api = authApis(token);
      let endpoint = "";
      let params = {};

      if (statType === "month") {
        endpoint = endpoints.revenue_monthly;
        params = { year };
      } else if (statType === "quarter") {
        endpoint = endpoints.revenue_quarterly;
        params = { year };
      } else {
        endpoint = endpoints.revenue_yearly;
        params = { fromYear, toYear };
      }

      const response = await api.get(endpoint, { params });
      const data = response.data; // List of Object[] [period, count, totalAmount]
      
      const formattedData = data.map(item => ({
        period: item[0],
        count: item[1],
        totalAmount: item[2]
      })).sort((a, b) => a.period - b.period);

      setStats(formattedData);

      // Calculate summary
      const totalRev = formattedData.reduce((acc, curr) => acc + curr.totalAmount, 0);
      const totalTrans = formattedData.reduce((acc, curr) => acc + curr.count, 0);
      setSummary({ totalRevenue: totalRev, totalTransactions: totalTrans });

    } catch (error) {
      console.error("Failed to fetch statistics:", error);
    } finally {
      setLoading(false);
    }
  }, [statType, year, fromYear, toYear]);

  useEffect(() => {
    fetchStats();
  }, [fetchStats]);

  const chartData = {
    labels: stats.map(s => {
      if (statType === "month") return `Tháng ${s.period}`;
      if (statType === "quarter") return `Quý ${s.period}`;
      return `Năm ${s.period}`;
    }),
    datasets: [
      {
        type: 'bar',
        label: 'Doanh thu (VNĐ)',
        data: stats.map(s => s.totalAmount),
        backgroundColor: 'rgba(79, 70, 229, 0.6)',
        borderColor: 'rgb(79, 70, 229)',
        borderWidth: 1,
        borderRadius: 8,
        yAxisID: 'y',
      },
      {
        type: 'line',
        label: 'Số giao dịch',
        data: stats.map(s => s.count),
        borderColor: 'rgb(16, 185, 129)',
        backgroundColor: 'rgba(16, 185, 129, 0.1)',
        borderWidth: 3,
        pointBackgroundColor: 'rgb(16, 185, 129)',
        fill: true,
        tension: 0.4,
        yAxisID: 'y1',
      }
    ]
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: { usePointStyle: true, boxWidth: 6 }
      },
      tooltip: {
        backgroundColor: 'rgba(15, 23, 42, 0.9)',
        padding: 12,
        cornerRadius: 8,
      }
    },
    scales: {
      y: {
        type: 'linear',
        display: true,
        position: 'left',
        grid: { drawOnChartArea: false },
        title: { display: true, text: 'Doanh thu (VNĐ)' }
      },
      y1: {
        type: 'linear',
        display: true,
        position: 'right',
        title: { display: true, text: 'Số giao dịch' }
      }
    }
  };

  const years = Array.from({ length: 10 }, (_, i) => new Date().getFullYear() - i);

  return (
    <AdminLayout>
      <div className="mb-4 d-flex justify-content-between align-items-center">
        <div>
          <h2 className="fw-bold mb-1">Dashboard Thống Kê</h2>
          <p className="text-muted mb-0">Theo dõi hiệu quả hoạt động kinh doanh của hệ thống.</p>
        </div>
        <Button variant="outline-primary" className="d-flex align-items-center gap-2 rounded-pill px-4" onClick={fetchStats}>
          <Activity size={18} />
          Làm mới
        </Button>
      </div>

      {/* Filter Section */}
      <Card className="border-0 shadow-sm rounded-4 mb-4 overflow-hidden">
        <Card.Body className="bg-white p-4">
          <Row className="align-items-end g-3">
            <Col md={3}>
              <Form.Group>
                <Form.Label className="fw-bold small text-uppercase text-muted mb-2">Loại thống kê</Form.Label>
                <Form.Select 
                  value={statType} 
                  onChange={(e) => setStatType(e.target.value)}
                  className="rounded-3 border-light shadow-none"
                >
                  <option value="month">Theo tháng</option>
                  <option value="quarter">Theo quý</option>
                  <option value="year">Theo năm</option>
                </Form.Select>
              </Form.Group>
            </Col>
            
            {statType !== "year" ? (
              <Col md={3}>
                <Form.Group>
                  <Form.Label className="fw-bold small text-uppercase text-muted mb-2">Năm</Form.Label>
                  <Form.Select 
                    value={year} 
                    onChange={(e) => setYear(e.target.value)}
                    className="rounded-3 border-light shadow-none"
                  >
                    {years.map(y => <option key={y} value={y}>{y}</option>)}
                  </Form.Select>
                </Form.Group>
              </Col>
            ) : (
              <>
                <Col md={3}>
                  <Form.Group>
                    <Form.Label className="fw-bold small text-uppercase text-muted mb-2">Từ năm</Form.Label>
                    <Form.Select 
                      value={fromYear} 
                      onChange={(e) => setFromYear(e.target.value)}
                      className="rounded-3 border-light shadow-none"
                    >
                      {years.map(y => <option key={y} value={y}>{y}</option>)}
                    </Form.Select>
                  </Form.Group>
                </Col>
                <Col md={3}>
                  <Form.Group>
                    <Form.Label className="fw-bold small text-uppercase text-muted mb-2">Đến năm</Form.Label>
                    <Form.Select 
                      value={toYear} 
                      onChange={(e) => setToYear(e.target.value)}
                      className="rounded-3 border-light shadow-none"
                    >
                      {years.map(y => <option key={y} value={y}>{y}</option>)}
                    </Form.Select>
                  </Form.Group>
                </Col>
              </>
            )}
            <Col md={3}>
              <Button variant="primary" className="w-100 rounded-3 d-flex align-items-center justify-content-center gap-2" onClick={fetchStats}>
                <Filter size={18} />
                Lọc dữ liệu
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Summary Cards */}
      <Row className="g-4 mb-4">
        {[
          { title: "Tổng doanh thu", value: `${summary.totalRevenue.toLocaleString()} VNĐ`, icon: <DollarSign className="text-primary" />, color: "primary" },
          { title: "Số lượng giao dịch", value: summary.totalTransactions, icon: <TrendingUp className="text-success" />, color: "success" },
          { title: "Doanh thu trung bình", value: `${(summary.totalTransactions ? Math.round(summary.totalRevenue / summary.totalTransactions) : 0).toLocaleString()} VNĐ`, icon: <Activity className="text-info" />, color: "info" },
          { title: "Kỳ thống kê", value: stats.length, icon: <Calendar className="text-warning" />, color: "warning" },
        ].map((item, idx) => (
          <Col md={3} key={idx}>
            <motion.div whileHover={{ y: -5 }}>
              <Card className="border-0 shadow-sm rounded-4 h-100 overflow-hidden">
                <Card.Body className="p-4">
                  <div className="d-flex justify-content-between align-items-center mb-3">
                    <div className={`p-2 rounded-3 bg-${item.color} bg-opacity-10`}>
                      {item.icon}
                    </div>
                  </div>
                  <h6 className="text-muted fw-bold small text-uppercase mb-2">{item.title}</h6>
                  <h3 className="fw-bold mb-0">{item.value}</h3>
                </Card.Body>
              </Card>
            </motion.div>
          </Col>
        ))}
      </Row>

      {/* Chart Section */}
      <Card className="border-0 shadow-sm rounded-4 overflow-hidden mb-4">
        <Card.Header className="bg-white border-0 p-4">
          <h5 className="fw-bold mb-0">Biểu đồ tăng trưởng doanh thu</h5>
        </Card.Header>
        <Card.Body className="p-4" style={{ height: '400px' }}>
          {loading ? (
            <div className="h-100 d-flex align-items-center justify-content-center">
              <Spinner animation="border" variant="primary" />
            </div>
          ) : (
            <Bar data={chartData} options={chartOptions} />
          )}
        </Card.Body>
      </Card>
    </AdminLayout>
  );
};

export default AdminDashboard;
