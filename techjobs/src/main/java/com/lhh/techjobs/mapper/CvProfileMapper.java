package com.lhh.techjobs.mapper;

import com.lhh.techjobs.dto.request.CandidateCreationRequest;
import com.lhh.techjobs.dto.response.CvExtractedResponse;
import com.lhh.techjobs.entity.CvProfile;
import com.lhh.techjobs.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CvProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "rawText", ignore = true)
    CvProfile toCvProfile(CvExtractedResponse request);
}
