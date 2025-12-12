package domain.backend.report.domain.enums;

public enum ActivityEvent {
    APPROVE,         // 관리자 승인
    REJECT,          // 관리자 반려
    START_SURVEY,    // 답사 시작
    FINISH_SURVEY,   // 답사 완료
    START_RECRUIT,   // 모집 시작
    START_ACTIVITY,  // 활동 시작
    FINISH_ACTIVITY, // 활동 완료
    CANCEL,           // 취소(모든 상태에서 가능)
    REJECT_DURING_SURVEY,   // 답사 중 반려

    COLLAB_REQUEST,         // 연합 활동 요청
    COLLAB_APPROVE,
    COLLAB_REJECT,


    }
