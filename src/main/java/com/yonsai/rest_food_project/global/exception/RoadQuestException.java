package com.yonsai.rest_food_project.global.exception;


/** service 로직에서 throw new RoadQuestException("")으로 예외처리하면 java가 보관해서 가지고 있다가 핸들러에 보내줍니다 그럼 핸들러가 e.getMessage로 출력합니다 */
public class RoadQuestException extends RuntimeException {

    public RoadQuestException(String message) {
        super(message);
    }

}
