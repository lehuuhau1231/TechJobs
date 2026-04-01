import React, { useEffect, useState, useCallback } from "react";
import { Button, Form, InputGroup } from "react-bootstrap";
import { Eraser } from "lucide-react";
import Apis, { endpoints } from "../../../configs/Apis";

const INITIAL_FILTERS = {
  title: null,
  jobSkill: null,
  jobLevel: null,
  jobType: null,
  contractType: null,
  city: null,
};

const JobFilter = ({ onSearch, onReset }) => {
  const [filters, setFilters] = useState(INITIAL_FILTERS);

  const [jobLevels, setJobLevels] = useState([]);
  const [jobTypes, setJobTypes] = useState([]);
  const [contractTypes, setContractTypes] = useState([]);
  const [cities, setCities] = useState([]);

  useEffect(() => {
    fetchJobLevels();
    fetchJobTypes();
    fetchContractTypes();
    fetchCities();
  }, []);

  const fetchJobLevels = async () => {
    try {
      const response = await Apis.get(endpoints.job_levels);
      if (response.status === 200) setJobLevels(response.data);
    } catch (error) {
      console.error("Error fetching job levels:", error);
    }
  };

  const fetchJobTypes = async () => {
    try {
      const response = await Apis.get(endpoints.job_types);
      if (response.status === 200) setJobTypes(response.data);
    } catch (error) {
      console.error("Error fetching job types:", error);
    }
  };

  const fetchContractTypes = async () => {
    try {
      const response = await Apis.get(endpoints.contract_types);
      if (response.status === 200) setContractTypes(response.data);
    } catch (error) {
      console.error("Error fetching contract types:", error);
    }
  };

  const fetchCities = async () => {
    try {
      const response = await Apis.get(endpoints.cities);
      if (response.status === 200) setCities(response.data);
    } catch (error) {
      console.error("Error fetching cities:", error);
    }
  };

  const updateFilter = useCallback((key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value || null }));
  }, []);

  const handleSearch = () => {
    onSearch(filters);
  };

  const handleReset = () => {
    setFilters(INITIAL_FILTERS);
    onReset();
  };

  return (
    <div className='home-search-wrapper'>
      <div className='home-search-row'>
        <InputGroup className='home-search-input-wrapper'>
          <Form.Control
            type='text'
            placeholder='Tên công việc'
            className='home-search-input'
            value={filters.title || ""}
            onChange={(e) => updateFilter("title", e.target.value)}
          />
        </InputGroup>
        <Button className='home-search-btn' onClick={handleSearch}>
          Search
        </Button>
      </div>

      {/* Advanced Filters Row */}
      <div className='home-filters-row'>
        <Form.Select
          className='home-filter-select'
          value={filters.jobLevel || ""}
          onChange={(e) => updateFilter("jobLevel", e.target.value)}
        >
          <option value=''>Tất cả cấp bật</option>
          {jobLevels.map((level) => (
            <option key={level.id} value={level.name}>
              {level.name}
            </option>
          ))}
        </Form.Select>

        <Form.Select
          className='home-filter-select'
          value={filters.jobType || ""}
          onChange={(e) => updateFilter("jobType", e.target.value)}
        >
          <option value=''>Tất cả loại công việc</option>
          {jobTypes.map((type) => (
            <option key={type.id} value={type.name}>
              {type.name}
            </option>
          ))}
        </Form.Select>

        <Form.Select
          className='home-filter-select'
          value={filters.contractType || ""}
          onChange={(e) => updateFilter("contractType", e.target.value)}
        >
          <option value=''>Tất cả loại hợp đồng</option>
          {contractTypes.map((type) => (
            <option key={type.id} value={type.name}>
              {type.name}
            </option>
          ))}
        </Form.Select>

        <Form.Select
          className='home-filter-select'
          value={filters.city || ""}
          onChange={(e) => updateFilter("city", e.target.value)}
        >
          <option value=''>Tất cả thành phố</option>
          {cities.map((cityItem) => (
            <option key={cityItem.id} value={cityItem.name}>
              {cityItem.name}
            </option>
          ))}
        </Form.Select>

        <Button className='custom-button' onClick={handleReset}>
          <Eraser size={16} />
          Xóa bộ lọc
        </Button>
      </div>
    </div>
  );
};

export default JobFilter;
