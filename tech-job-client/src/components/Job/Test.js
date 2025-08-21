import React, { useState } from "react";
import Header from "../layout/Header";

const Home = () => {
  const [selectedJobType, setSelectedJobType] = useState(["All"]);
  const [selectedSalaryRange, setSelectedSalaryRange] = useState("Custom");
  const [selectedExperience, setSelectedExperience] = useState(["All"]);
  const [viewMode, setViewMode] = useState("grid");

  const handleJobTypeChange = (type) => {
    if (type === "All") {
      setSelectedJobType(["All"]);
    } else {
      const newTypes = selectedJobType.includes("All")
        ? [type]
        : selectedJobType.includes(type)
        ? selectedJobType.filter((t) => t !== type)
        : [...selectedJobType.filter((t) => t !== "All"), type];

      setSelectedJobType(newTypes.length === 0 ? ["All"] : newTypes);
    }
  };

  const handleExperienceChange = (exp) => {
    if (exp === "All") {
      setSelectedExperience(["All"]);
    } else {
      const newExp = selectedExperience.includes("All")
        ? [exp]
        : selectedExperience.includes(exp)
        ? selectedExperience.filter((e) => e !== exp)
        : [...selectedExperience.filter((e) => e !== "All"), exp];

      setSelectedExperience(newExp.length === 0 ? ["All"] : newExp);
    }
  };

  return (
    <div style={{ backgroundColor: "#ffffff", minHeight: "100vh" }}>
      {/* Header - Very simple like in image */}
      <Header />

      <div style={{ display: "flex", minHeight: "calc(100vh - 60px)" }}>
        {/* Left Sidebar - Exactly like in image */}
        <div
          style={{
            width: "250px",
            backgroundColor: "#ffffff",
            padding: "20px",
            borderRight: "1px solid #f3f4f6",
          }}
        >
          {/* Filter Header */}
          <div style={{ marginBottom: "20px" }}>
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <span style={{ fontSize: "14px", fontWeight: "600" }}>
                Filter
              </span>
              <button
                style={{
                  background: "none",
                  border: "none",
                  color: "#4F46E5",
                  fontSize: "12px",
                  cursor: "pointer",
                }}
              >
                Clear All
              </button>
            </div>
          </div>

          {/* Job Type */}
          <div style={{ marginBottom: "30px" }}>
            <h6
              style={{
                fontSize: "14px",
                fontWeight: "600",
                marginBottom: "12px",
              }}
            >
              Job Type
            </h6>

            {/* All */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='checkbox'
                id='all-job'
                checked={selectedJobType.includes("All")}
                onChange={() => handleJobTypeChange("All")}
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='all-job'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                All
              </label>
            </div>

            {/* Contract */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
            >
              <div style={{ display: "flex", alignItems: "center" }}>
                <input
                  type='checkbox'
                  id='contract'
                  checked={selectedJobType.includes("Contract")}
                  onChange={() => handleJobTypeChange("Contract")}
                  style={{ marginRight: "8px" }}
                />
                <label
                  htmlFor='contract'
                  style={{ fontSize: "13px", cursor: "pointer" }}
                >
                  Contract
                </label>
              </div>
              <span style={{ fontSize: "12px", color: "#6b7280" }}>(600)</span>
            </div>

            {/* Full Time */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
            >
              <div style={{ display: "flex", alignItems: "center" }}>
                <input
                  type='checkbox'
                  id='fulltime'
                  checked={selectedJobType.includes("Full Time")}
                  onChange={() => handleJobTypeChange("Full Time")}
                  style={{ marginRight: "8px" }}
                />
                <label
                  htmlFor='fulltime'
                  style={{ fontSize: "13px", cursor: "pointer" }}
                >
                  Full Time
                </label>
              </div>
              <span style={{ fontSize: "12px", color: "#6b7280" }}>(1500)</span>
            </div>

            {/* Part Time */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
            >
              <div style={{ display: "flex", alignItems: "center" }}>
                <input
                  type='checkbox'
                  id='parttime'
                  checked={selectedJobType.includes("Part Time")}
                  onChange={() => handleJobTypeChange("Part Time")}
                  style={{ marginRight: "8px" }}
                />
                <label
                  htmlFor='parttime'
                  style={{ fontSize: "13px", cursor: "pointer" }}
                >
                  Part Time
                </label>
              </div>
              <span style={{ fontSize: "12px", color: "#6b7280" }}>(1100)</span>
            </div>

            {/* Internship */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
            >
              <div style={{ display: "flex", alignItems: "center" }}>
                <input
                  type='checkbox'
                  id='internship'
                  checked={selectedJobType.includes("Internship")}
                  onChange={() => handleJobTypeChange("Internship")}
                  style={{ marginRight: "8px" }}
                />
                <label
                  htmlFor='internship'
                  style={{ fontSize: "13px", cursor: "pointer" }}
                >
                  Internship
                </label>
              </div>
              <span style={{ fontSize: "12px", color: "#6b7280" }}>(140)</span>
            </div>

            {/* Open to Remote */}
            <div
              style={{
                marginTop: "12px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='checkbox'
                id='remote'
                defaultChecked
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='remote'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                Open to Remote
              </label>
            </div>
          </div>

          {/* Salary Range */}
          <div style={{ marginBottom: "30px" }}>
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                marginBottom: "12px",
              }}
            >
              <h6 style={{ fontSize: "14px", fontWeight: "600", margin: "0" }}>
                Salary Range
              </h6>
              <button
                style={{
                  background: "none",
                  border: "none",
                  color: "#4F46E5",
                  fontSize: "12px",
                  cursor: "pointer",
                }}
              >
                Reset
              </button>
            </div>

            {/* Less than $1000 */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='radio'
                name='salary'
                id='less1000'
                checked={selectedSalaryRange === "Less than $1000"}
                onChange={() => setSelectedSalaryRange("Less than $1000")}
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='less1000'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                Less than $1000
              </label>
            </div>

            {/* $1000 - $4000 */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='radio'
                name='salary'
                id='range1000'
                checked={selectedSalaryRange === "$1000 - $4000"}
                onChange={() => setSelectedSalaryRange("$1000 - $4000")}
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='range1000'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                $1000 - $4000
              </label>
            </div>

            {/* More than $4000 */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='radio'
                name='salary'
                id='more4000'
                checked={selectedSalaryRange === "More than $4000"}
                onChange={() => setSelectedSalaryRange("More than $4000")}
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='more4000'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                More than $4000
              </label>
            </div>

            {/* Custom */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='radio'
                name='salary'
                id='custom'
                checked={selectedSalaryRange === "Custom"}
                onChange={() => setSelectedSalaryRange("Custom")}
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='custom'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                Custom
              </label>
            </div>

            {/* Range Slider for Custom */}
            {selectedSalaryRange === "Custom" && (
              <div style={{ marginTop: "12px", paddingLeft: "24px" }}>
                <input
                  type='range'
                  min='0'
                  max='6000'
                  defaultValue='0'
                  style={{ width: "100%", marginBottom: "4px" }}
                />
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    fontSize: "11px",
                    color: "#6b7280",
                  }}
                >
                  <span>$0</span>
                  <span>$6,000</span>
                </div>
              </div>
            )}
          </div>

          {/* Experience */}
          <div>
            <h6
              style={{
                fontSize: "14px",
                fontWeight: "600",
                marginBottom: "12px",
              }}
            >
              Experience
            </h6>

            {/* All */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='checkbox'
                id='all-exp'
                checked={selectedExperience.includes("All")}
                onChange={() => handleExperienceChange("All")}
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='all-exp'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                All
              </label>
            </div>

            {/* Less than a year */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <input
                type='checkbox'
                id='less-year'
                checked={selectedExperience.includes("Less than a year")}
                onChange={() => handleExperienceChange("Less than a year")}
                style={{ marginRight: "8px" }}
              />
              <label
                htmlFor='less-year'
                style={{ fontSize: "13px", cursor: "pointer" }}
              >
                Less than a year
              </label>
            </div>

            {/* 1-2 years */}
            <div
              style={{
                marginBottom: "8px",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
              }}
            >
              <div style={{ display: "flex", alignItems: "center" }}>
                <input
                  type='checkbox'
                  id='1-2years'
                  checked={selectedExperience.includes("1-2 years")}
                  onChange={() => handleExperienceChange("1-2 years")}
                  style={{ marginRight: "8px" }}
                />
                <label
                  htmlFor='1-2years'
                  style={{ fontSize: "13px", cursor: "pointer" }}
                >
                  1-2 years
                </label>
              </div>
              <span style={{ fontSize: "12px", color: "#6b7280" }}>(120)</span>
            </div>
          </div>
        </div>

        {/* Main Content Area - Right side */}
        <div style={{ flex: 1, backgroundColor: "#f9fafb", padding: "20px" }}>
          {/* This is where job listings would go - for now showing the main content area */}
          <div
            style={{
              height: "100%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "#6b7280",
              fontSize: "14px",
            }}
          >
            <div style={{ textAlign: "center" }}>
              <p>Job listings would appear here</p>
              <p style={{ fontSize: "12px", marginTop: "8px" }}>
                (Main content area - search bar and job cards would be
                implemented here)
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
