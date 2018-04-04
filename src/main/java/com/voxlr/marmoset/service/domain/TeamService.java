package com.voxlr.marmoset.service.domain;

import com.voxlr.marmoset.model.persistence.Company;
import com.voxlr.marmoset.model.persistence.Team;
import com.voxlr.marmoset.model.persistence.dto.TeamCreateDTO;
import com.voxlr.marmoset.repositories.TeamRepository;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
  @Autowired ModelMapper modelMapper;

  @Autowired TeamRepository teamRepository;

  public boolean validateExists(ObjectId teamId) {
    return teamRepository.findIdById(teamId) != null;
  }

  public Team createTeamInternal(Company company, TeamCreateDTO teamCreateDTO) {
    Team team = modelMapper.map(teamCreateDTO, Team.class);
    team.setCompanyId(company.getId());
    team = teamRepository.save(team);
    return team;
  }
}
