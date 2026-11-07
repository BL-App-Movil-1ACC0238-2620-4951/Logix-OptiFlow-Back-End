package com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.repositories;

import com.optiflow.platform.searchbooking.domain.model.entities.StaffMember;
import com.optiflow.platform.searchbooking.domain.repositories.StaffMemberRepository;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.EmailAddress;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.OpticalStoreId;
import com.optiflow.platform.searchbooking.domain.model.valueobjects.StaffMemberId;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.entities.StaffMemberEntity;
import com.optiflow.platform.searchbooking.infrastructure.persistence.jpa.mappers.StaffMemberMapper;
import com.optiflow.platform.shared.domain.exceptions.DomainException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class StaffMemberRepositoryImpl implements StaffMemberRepository {

  private final StaffMemberJpaRepository staffMemberJpaRepository;
  private final StaffMemberMapper staffMemberMapper;

  public StaffMemberRepositoryImpl(
      StaffMemberJpaRepository staffMemberJpaRepository, StaffMemberMapper staffMemberMapper) {
    this.staffMemberJpaRepository = staffMemberJpaRepository;
    this.staffMemberMapper = staffMemberMapper;
  }

  @Override
  public void save(StaffMember staffMember, String passwordHash) {
    staffMemberJpaRepository.save(staffMemberMapper.toEntity(staffMember, passwordHash));
  }

  @Override
  public void update(StaffMember staffMember) {
    StaffMemberEntity entity = staffMemberJpaRepository
        .findById(staffMember.id().value())
        .orElseThrow(() -> new DomainException("Staff member was not found.", 404));
    staffMemberMapper.toEntity(staffMember, entity);
    staffMemberJpaRepository.save(entity);
  }

  @Override
  public Optional<StaffMember> findById(StaffMemberId id) {
    return staffMemberJpaRepository.findById(id.value()).map(staffMemberMapper::toDomain);
  }

  @Override
  public Optional<StaffMember> findByEmail(EmailAddress email) {
    return staffMemberJpaRepository.findByEmail(email.value()).map(staffMemberMapper::toDomain);
  }

  @Override
  public Optional<String> findPasswordHashByEmail(EmailAddress email) {
    return staffMemberJpaRepository.findByEmail(email.value()).map(StaffMemberEntity::getPasswordHash);
  }

  @Override
  public List<StaffMember> findByOpticalStoreId(OpticalStoreId opticalStoreId) {
    return staffMemberJpaRepository.findByOpticalStoreId(opticalStoreId.value()).stream()
        .map(staffMemberMapper::toDomain)
        .toList();
  }

  @Override
  public List<StaffMember> findAll() {
    return staffMemberJpaRepository.findAll().stream().map(staffMemberMapper::toDomain).toList();
  }
}
