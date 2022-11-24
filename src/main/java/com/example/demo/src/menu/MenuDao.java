package com.example.demo.src.menu;

import com.example.demo.src.menu.model.GetMenuRes;
import com.example.demo.src.menu.model.PatchMenuReq;
import com.example.demo.src.menu.model.PostMenuReq;
import com.example.demo.src.restautrant.model.GetRestRes;
import com.example.demo.src.restautrant.model.PatchRestReq;
import com.example.demo.src.restautrant.model.PostRestReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
public class MenuDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    /**
     * 생성
     */
    public int createMenu(PostMenuReq postMenuReq) {
        String createMenuQuery = "insert into Menu (menuIdx, restaurantIdx, name, price, imageUrl) VALUES (?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createMenuParams = new Object[]{
                postMenuReq.getMenuIdx(),
                postMenuReq.getRestaurantIdx(),
                postMenuReq.getName(),
                postMenuReq.getPrice(),
                postMenuReq.getImageUrl()
        }; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createMenuQuery, createMenuParams);

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    /**
     * 조회
     */
    public List<GetMenuRes> getMenuByRestaurantId(int restaurantIdx) {
        String getMenuByRestaurantIdQuery = "select * from Menu where restaurantIdx = ?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
        int getMenuByRestaurantIdParam = restaurantIdx;
        return this.jdbcTemplate.query(getMenuByRestaurantIdQuery,
                getRestResRowMapper(), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getMenuByRestaurantIdParam); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    public List<GetMenuRes> getAllMenu() {
        String getMenuByRestaurantIdQuery = "select * from Menu"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
        return this.jdbcTemplate.query(getMenuByRestaurantIdQuery,
                getRestResRowMapper() // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                ); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }


    // 해당 restaurantIdx로 조회
    public GetMenuRes getMenu(int menuIdx) {
        String getMenuQuery = "select * from Menu where menuIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getMenuParams = menuIdx;
        return this.jdbcTemplate.queryForObject(getMenuQuery,
                getRestResRowMapper(), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getMenuParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    private RowMapper<GetMenuRes> getRestResRowMapper() {
        return new RowMapper<GetMenuRes>() {
            @Override
            public GetMenuRes mapRow(ResultSet rs, int rowNum) throws SQLException {
                GetMenuRes getMenuRes = new GetMenuRes();
                getMenuRes.setMenuIdx(rs.getInt("menuIdx"));
                getMenuRes.setRestaurantIdx(rs.getInt("restaurantIdx"));
                getMenuRes.setName(rs.getString("name"));
                getMenuRes.setPrice(rs.getInt("price"));
                getMenuRes.setImageUrl(rs.getString("imageUrl"));
                return getMenuRes;
            }
        };
    }
        /**
         * 변경
         */

    // 메뉴 이름 변경
    public int modifyMenuName(PatchMenuReq patchMenuReq) {
        String modifyMenuNameQuery = "update Menu set name = ? where menuIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyMenuNameParams = new Object[]{patchMenuReq.getName(), patchMenuReq.getMenuIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyMenuNameQuery, modifyMenuNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }
    // 메뉴 가격 변경
    public int modifyMenuPrice(PatchMenuReq patchMenuReq) {
        String modifyMenuPriceQuery = "update Menu set price = ? where menuIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyMenuPriceParams = new Object[]{patchMenuReq.getPrice(), patchMenuReq.getMenuIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyMenuPriceQuery, modifyMenuPriceParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }
    // 메뉴 사진 변경
    public int modifyMenuImageUrl(PatchMenuReq patchMenuReq) {
        String modifyMenuImageUrlQuery = "update Menu set imageUrl = ? where menuIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyMenuImageUrlParams = new Object[]{patchMenuReq.getImageUrl(), patchMenuReq.getMenuIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyMenuImageUrlQuery, modifyMenuImageUrlParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }
    /**
     * 삭제
     */
    public int deleteMenu(int menuIdx){
        String deleteMenuQuery = "delete from Menu where menuIdx = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int deletMenuParams = menuIdx;
        return this.jdbcTemplate.update(deleteMenuQuery, deletMenuParams);
    }
}
