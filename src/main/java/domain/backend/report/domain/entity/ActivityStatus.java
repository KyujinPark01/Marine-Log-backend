package domain.backend.report.domain.entity;

public enum ActivityStatus {
    REPORTED,     // 제보 등록
    APPROVED,     // 제보 승인
    REJECTED,     // 제보 반려
    SURVEYING,    // 답사 중
    RECRUITING,   // 모집 중
    IN_PROGRESS,  // 진행 중
    DONE,         // 완료
    CANCELED      // 취소됨
}