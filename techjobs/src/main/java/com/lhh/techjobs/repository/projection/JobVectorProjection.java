package com.lhh.techjobs.repository.projection;

public interface JobVectorProjection {
    Integer getId();
    String getTitle();
    String getDescription();
    Integer getSalaryMin();
    Integer getSalaryMax();
    String getJobLevel();
    String getCity();
    String getDistrict();
    String getImage();
    String getSkills();
}
