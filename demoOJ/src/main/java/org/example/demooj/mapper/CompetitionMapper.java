package org.example.demooj.mapper;

import org.example.demooj.entity.CompetitionInformation;

import java.util.List;

public interface CompetitionMapper {

    Integer deleteCompetitionById(Integer id);

    CompetitionInformation selectCompetitionById(Integer id);

    List<CompetitionInformation> selectAllCompetition();

    Integer insertCompetition(CompetitionInformation competitionInformation);

    Integer updateCompetition(CompetitionInformation competitionInformation);
}
