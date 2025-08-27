package com.lhh.techjobs.mapper;

import com.lhh.techjobs.dto.request.CandidateCreationRequest;
import com.lhh.techjobs.dto.request.EmployerCreationRequest;
import com.lhh.techjobs.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", constant = "CANDIDATE")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    User toUserForCandidate(CandidateCreationRequest request);

    @Mapping(target = "role", constant = "EMPLOYER")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "employer", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    User toUserForEmployer(EmployerCreationRequest request);
}
