package com.example.demo.src.restautrant;


import com.example.demo.config.BaseException;
import com.example.demo.src.restautrant.model.GetRestRes;
import com.example.demo.src.restautrant.model.PatchRestReq;
import com.example.demo.src.restautrant.model.PostRestReq;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RestaurantDao {


    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    /**
     * 생성
     */
    public int createRestaurant(PostRestReq postRestReq) {
        String createRestQuery = "insert into Restaurant (ownerIdx, name, category, minimumPaymentCost, imageUrl) VALUES (?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createRestParams = new Object[]{
                postRestReq.getOwnerIdx(),
                postRestReq.getName(),
                postRestReq.getCategory(),
                postRestReq.getMinimumPaymentCost(),
                postRestReq.getImageUrl(),
        }; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createRestQuery, createRestParams);
        // email -> postUserReq.getEmail(), password -> postUserReq.getPassword(), nickname -> postUserReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 User Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    /**
     * 변경
     */

    // 레스토랑 이름 변경
    public int modifyRestaurantName(PatchRestReq patchRestReq) {
        String modifyUserNameQuery = "update Restaurant set name = ? where restaurantIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchRestReq.getName(), patchRestReq.getRestaurantIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }
    // 카테고리 변경
    public int modifyRestaurantCategory(PatchRestReq patchRestReq) {
        String modifyUserNameQuery = "update Restaurant set category = ? where restaurantIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchRestReq.getCategory(), patchRestReq.getRestaurantIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 최소 주분 금액 변경
    public int modifyRestaurantMinimumPaymentCost(PatchRestReq patchRestReq) {
        String modifyUserNameQuery = "update Restaurant set minimumPaymentCost = ? where restaurantIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchRestReq.getMinimumPaymentCost(), patchRestReq.getRestaurantIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 가게 이미지 주소 변경
    public int modifyRestaurantImageUrl(PatchRestReq patchRestReq) {
        String modifyUserNameQuery = "update Restaurant set imageUrl = ? where restaurantIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchRestReq.getImageUrl(), patchRestReq.getRestaurantIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    /**
     * 조회
     */
    // User 테이블에 존재하는 전체 유저들의 정보 조회
    public List<GetRestRes> getRestaurants() {
        String getRestaurantsQuery = "select * from Restaurant"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getRestaurantsQuery,
                getRestResRowMapper()  // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
    }

    // 해당 name을 갖는 가게들 정보 조회
    public List<GetRestRes> getRestaurantByCategory(String category) {
        System.out.println("Dao - Category");
        String getRestByCategoryQuery = "select * from Restaurant where category=?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
        String getRestByCategoryParams = category;
        return this.jdbcTemplate.query(getRestByCategoryQuery,
                getRestResRowMapper(), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getRestByCategoryParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }
    // 특정 사장님이 갖는 가게들 정보 조회
    public List<GetRestRes> getRestaurantsByOwnerId(int ownerIdx) {
        String getRestByOwnerIdQuery = "select * from Restaurant where ownerIdx = ?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
        int getRestByOwnerIdParams = ownerIdx;
        return this.jdbcTemplate.query(getRestByOwnerIdQuery,
                getRestResRowMapper(), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getRestByOwnerIdParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 restaurantIdx로 조회
    public GetRestRes getRestaurant(int restaurantIdx) {
        System.out.println("Dao - no category");
        String getRestQuery = "select * from Restaurant where restaurantIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getRestParams = restaurantIdx;
        return this.jdbcTemplate.queryForObject(getRestQuery,
                getRestResRowMapper(), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getRestParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 메뉴 수정 및 변경을 위해 레스토랑 idx로 사장의 idx를 반환한다.
    public int getOwnerIdByRestaurantId(int restaurantIdx){
        System.out.println("check1");
        String getOwnerIdQuery = "select * from Restaurant where restaurantIdx = ?";
        int getOwnerIdParam = restaurantIdx;
        GetRestRes getRestRes = this.jdbcTemplate.queryForObject(getOwnerIdQuery, getRestResRowMapper(),getOwnerIdParam);
        return getRestRes.getOwnerIdx();
    }

    private RowMapper<GetRestRes> getRestResRowMapper(){
        return new RowMapper<GetRestRes>() {
            @Override
            public GetRestRes mapRow(ResultSet rs, int rowNum) throws SQLException {
                GetRestRes getRestRes = new GetRestRes();
                getRestRes.setOwnerIdx(rs.getInt("ownerIdx"));
                getRestRes.setRestaurantIdx(rs.getInt("restaurantIdx"));
                getRestRes.setName(rs.getString("name"));
                getRestRes.setCategory(rs.getString("category"));
                getRestRes.setMinimumPaymentCost(rs.getInt("minimumPaymentCost"));
                getRestRes.setImageUrl(rs.getString("imageUrl"));
                getRestRes.setDeliveryTime(rs.getInt("deliveryTime"));
                getRestRes.setDeliveryCost(rs.getInt("deliveryCost"));
                return getRestRes;
            }
        };
    }

    /**
     * 삭제
     */
    public int deleteRestaurant(int restaurantIdx){
        String deleteRestQuery = "delete from Restaurant where restaurantIdx = ?";
        int deleteRestParams = restaurantIdx;
        return this.jdbcTemplate.update(deleteRestQuery, deleteRestParams);
    }
}
