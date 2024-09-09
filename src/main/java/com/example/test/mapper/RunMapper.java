package com.example.test.mapper;

import com.example.test.dto.RunDTO;
import com.example.test.model.Run;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RunMapper {

    RunDTO toRunDTO(Run run);

    Run toRun(RunDTO runDTO);
}
