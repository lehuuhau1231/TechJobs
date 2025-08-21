import {
  Bell,
  Building2,
  MessageCircle,
  Search,
  Settings,
  TrendingUp,
  Users,
} from "lucide-react";
import { Container } from "react-bootstrap";

const Header = () => {
  return (
    <Container>
      <div
        style={{
          padding: "12px 20px",
          backgroundColor: "#ffffff",
          borderBottom: "1px solid #e5e7eb",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <div style={{ display: "flex", alignItems: "center" }}>
          <span style={{ fontSize: "20px", marginRight: "8px" }}>⚡</span>
          <span style={{ fontWeight: "bold", fontSize: "16px" }}>
            MyJobs.id
          </span>
        </div>

        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <button
            style={{
              padding: "6px 12px",
              backgroundColor: "#4F46E5",
              color: "white",
              border: "none",
              borderRadius: "4px",
              fontSize: "12px",
              display: "flex",
              alignItems: "center",
              gap: "4px",
            }}
          >
            <Search size={12} />
            Find Jobs
          </button>
          <button
            style={{
              padding: "6px 12px",
              backgroundColor: "transparent",
              color: "#6b7280",
              border: "1px solid #d1d5db",
              borderRadius: "4px",
              fontSize: "12px",
              display: "flex",
              alignItems: "center",
              gap: "4px",
            }}
          >
            <Users size={12} />
            Find Talent
          </button>
          <button
            style={{
              padding: "6px 12px",
              backgroundColor: "transparent",
              color: "#6b7280",
              border: "1px solid #d1d5db",
              borderRadius: "4px",
              fontSize: "12px",
              display: "flex",
              alignItems: "center",
              gap: "4px",
            }}
          >
            <Building2 size={12} />
            Community
          </button>
          <button
            style={{
              padding: "6px 12px",
              backgroundColor: "transparent",
              color: "#6b7280",
              border: "1px solid #d1d5db",
              borderRadius: "4px",
              fontSize: "12px",
              display: "flex",
              alignItems: "center",
              gap: "4px",
            }}
          >
            <TrendingUp size={12} />
            Upload Job
          </button>
          <Bell size={16} style={{ color: "#6b7280", cursor: "pointer" }} />
          <MessageCircle
            size={16}
            style={{ color: "#6b7280", cursor: "pointer" }}
          />
          <Settings size={16} style={{ color: "#6b7280", cursor: "pointer" }} />
          <div
            style={{
              width: "24px",
              height: "24px",
              backgroundColor: "#4F46E5",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "white",
              fontSize: "12px",
              fontWeight: "bold",
            }}
          >
            A
          </div>
        </div>
      </div>
    </Container>
  );
};

export default Header;
