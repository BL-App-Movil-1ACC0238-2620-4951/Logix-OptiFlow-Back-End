package com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "optical_prescriptions")
public class OpticalPrescriptionEntity {

  @Id
  @Column(name = "clinical_record_id")
  private UUID clinicalRecordId;

  @Column(name = "sphere_od", nullable = false, precision = 5, scale = 2)
  private BigDecimal sphereOd;

  @Column(name = "sphere_os", nullable = false, precision = 5, scale = 2)
  private BigDecimal sphereOs;

  @Column(name = "cylinder_od", nullable = false, precision = 5, scale = 2)
  private BigDecimal cylinderOd;

  @Column(name = "cylinder_os", nullable = false, precision = 5, scale = 2)
  private BigDecimal cylinderOs;

  @Column(name = "axis_od", nullable = false)
  private Short axisOd;

  @Column(name = "axis_os", nullable = false)
  private Short axisOs;

  @Column(precision = 5, scale = 2)
  private BigDecimal addition;

  @Column(length = 255)
  private String treatment;

  @Column(name = "recommended_frame_type", length = 255)
  private String recommendedFrameType;

  public UUID getClinicalRecordId() {
    return clinicalRecordId;
  }

  public void setClinicalRecordId(UUID clinicalRecordId) {
    this.clinicalRecordId = clinicalRecordId;
  }

  public BigDecimal getSphereOd() {
    return sphereOd;
  }

  public void setSphereOd(BigDecimal sphereOd) {
    this.sphereOd = sphereOd;
  }

  public BigDecimal getSphereOs() {
    return sphereOs;
  }

  public void setSphereOs(BigDecimal sphereOs) {
    this.sphereOs = sphereOs;
  }

  public BigDecimal getCylinderOd() {
    return cylinderOd;
  }

  public void setCylinderOd(BigDecimal cylinderOd) {
    this.cylinderOd = cylinderOd;
  }

  public BigDecimal getCylinderOs() {
    return cylinderOs;
  }

  public void setCylinderOs(BigDecimal cylinderOs) {
    this.cylinderOs = cylinderOs;
  }

  public Short getAxisOd() {
    return axisOd;
  }

  public void setAxisOd(Short axisOd) {
    this.axisOd = axisOd;
  }

  public Short getAxisOs() {
    return axisOs;
  }

  public void setAxisOs(Short axisOs) {
    this.axisOs = axisOs;
  }

  public BigDecimal getAddition() {
    return addition;
  }

  public void setAddition(BigDecimal addition) {
    this.addition = addition;
  }

  public String getTreatment() {
    return treatment;
  }

  public void setTreatment(String treatment) {
    this.treatment = treatment;
  }

  public String getRecommendedFrameType() {
    return recommendedFrameType;
  }

  public void setRecommendedFrameType(String recommendedFrameType) {
    this.recommendedFrameType = recommendedFrameType;
  }
}
