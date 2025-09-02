// Biến toàn cục lưu biểu đồ
let revenueChart;

// Khởi tạo khi trang được load
document.addEventListener('DOMContentLoaded', function() {
    // Thiết lập sự kiện khi chọn loại thống kê
    document.getElementById('statType').addEventListener('change', handleStatTypeChange);

    // Thiết lập sự kiện khi nhấn nút lọc
    document.getElementById('btnFilter').addEventListener('click', fetchRevenueData);

    // Lấy dữ liệu ban đầu (theo tháng trong năm hiện tại)
    fetchRevenueData();
});

// Xử lý khi thay đổi loại thống kê
function handleStatTypeChange() {
    const statType = document.getElementById('statType').value;
    const yearFilterContainer = document.getElementById('yearFilterContainer');

    // Hiển thị hoặc ẩn bộ lọc năm tùy vào loại thống kê
    if (statType === 'year') {
        yearFilterContainer.style.display = 'none';
    } else {
        yearFilterContainer.style.display = 'block';
    }
}

// Định dạng tiền Việt Nam
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        maximumFractionDigits: 0
    }).format(amount);
}

// Lấy dữ liệu thống kê doanh thu
function fetchRevenueData() {
    const statType = document.getElementById('statType').value;
    const yearFilter = document.getElementById('yearFilter').value;

    let apiUrl = '/techJobs/api/bills/stats';
    let params = {};

    // Thiết lập tham số dựa vào loại thống kê
    if (statType === 'month') {
        apiUrl = '/techJobs/api/bills/stats/monthly';
        params.year = yearFilter;
    } else if (statType === 'quarter') {
        apiUrl = '/techJobs/api/bills/stats/quarterly';
        params.year = yearFilter;
    } else if (statType === 'year') {
        apiUrl = '/techJobs/api/bills/stats/yearly';
        params.fromYear = 2020;
        params.toYear = new Date().getFullYear();
    }

    // Gửi yêu cầu API
    axios.get(apiUrl, { params: params })
        .then(response => {
            updateChart(response.data, statType);
        })
        .catch(error => {
            console.error('Lỗi khi lấy dữ liệu:', error);
            alert('Có lỗi xảy ra khi tải dữ liệu thống kê');
        });
}

// Reset bảng điều khiển khi không có dữ liệu
function resetDashboard() {
    document.getElementById('totalRevenue').textContent = '0 VNĐ';
    document.getElementById('totalTransactions').textContent = '0';
    document.getElementById('averageRevenue').textContent = '0 VNĐ';

    if (revenueChart) {
        revenueChart.destroy();
    }

    const ctx = document.getElementById('revenueChart').getContext('2d');
    revenueChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Doanh thu',
                data: [],
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgb(54, 162, 235)',
                borderWidth: 1
            }]
        }
    });
}

// Cập nhật biểu đồ với dữ liệu mới
function updateChart(data, statType) {
    // Chuẩn bị dữ liệu cho biểu đồ
    let labels = [];
    let amounts = [];
    let transactionCounts = [];

    console.log("data: ", data);
    console.log("statType: ", statType);

    // Xử lý dữ liệu dựa vào loại thống kê
    data.sort((a, b) => a[0] - b[0]);
    if (statType === 'month') {
        data.forEach(item => {
            labels.push('Tháng ' + item[0]);
            amounts.push(item[2]);
            transactionCounts.push(item[1]);
        });
    } else if (statType === 'quarter') {

        data.forEach(item => {
            labels.push('Quý ' + item[0]);
            amounts.push(item[2]);
            transactionCounts.push(item[1]);
        });
    } else if (statType === 'year') {

        data.forEach(item => {
            labels.push('Năm ' + item[0]);
            amounts.push(item[2]);
            transactionCounts.push(item[1]);
        });
    }
    console.log("labels: ", labels);
    console.log("amount: ", amounts);
    console.log("transactionCounts: ", transactionCounts);
    // Xóa biểu đồ cũ nếu đã tồn tại
    if (revenueChart) {
        revenueChart.destroy();
    }

    // Tạo biểu đồ mới
    const ctx = document.getElementById('revenueChart').getContext('2d');
    revenueChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Doanh thu (VNĐ)',
                    data: amounts,
                    backgroundColor: 'rgba(54, 162, 235, 0.5)',
                    borderColor: 'rgb(54, 162, 235)',
                    borderWidth: 1,
                    yAxisID: 'y'
                },
                {
                    label: 'Số lượng giao dịch',
                    data: transactionCounts,
                    backgroundColor: 'rgba(255, 99, 132, 0.5)',
                    borderColor: 'rgb(255, 99, 132)',
                    borderWidth: 1,
                    type: 'line',
                    yAxisID: 'y1'
                }
            ]
        },
        options: {
            responsive: true,
            interaction: {
                mode: 'index',
                intersect: false
            },
            scales: {
                y: {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: {
                        display: true,
                        text: 'Doanh thu (VNĐ)'
                    }
                },
                y1: {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    title: {
                        display: true,
                        text: 'Số lượng giao dịch'
                    },
                    grid: {
                        drawOnChartArea: false
                    }
                }
            }
        }
    });
}
