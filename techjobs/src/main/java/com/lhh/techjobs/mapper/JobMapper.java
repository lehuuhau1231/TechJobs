package com.lhh.techjobs.mapper;

import com.lhh.techjobs.dto.request.JobCreateRequest;
import com.lhh.techjobs.entity.Job;
import com.lhh.techjobs.enums.Status;
import com.lhh.techjobs.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {Status.class, LocalDateTime.class})
public interface JobMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(Status.PENDING)")
    @Mapping(target = "createdDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "postedDate", ignore = true)
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "jobLevel", ignore = true)
    @Mapping(target = "jobType", ignore = true)
    @Mapping(target = "contractType", ignore = true)
    @Mapping(target = "skills", ignore = true)
    Job toJob(JobCreateRequest request);
}
