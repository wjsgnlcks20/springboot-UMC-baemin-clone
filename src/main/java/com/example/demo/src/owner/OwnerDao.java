package com.example.demo.src.owner;


import com.example.demo.config.BaseException;
import com.example.demo.src.owner.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class OwnerDao {

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    /**
     * DAO관련 함수코드의 전반부는 크게 String ~~~Query와 Object[] ~~~~Params, jdbcTemplate함수로 구성되어 있습니다.(보통은 동적 쿼리문이지만, 동적쿼리가 아닐 경우, Params부분은 없어도 됩니다.)
     * Query부분은 DB에 SQL요청을 할 쿼리문을 의미하는데, 대부분의 경우 동적 쿼리(실행할 때 값이 주입되어야 하는 쿼리) 형태입니다.
     * 그래서 Query의 동적 쿼리에 입력되어야 할 값들이 필요한데 그것이 Params부분입니다.
     * Params부분은 클라이언트의 요청에서 제공하는 정보(~~~~Req.java에 있는 정보)로 부터 getXXX를 통해 값을 가져옵니다. ex) getEmail -> email값을 가져옵니다.
     *      Notice! get과 get의 대상은 카멜케이스로 작성됩니다. ex) item -> getItem, password -> getPassword, email -> getEmail, OwnerIdx -> getOwnerIdx
     * 그 다음 GET, POST, PATCH 메소드에 따라 jabcTemplate의 적절한 함수(queryForObject, query, update)를 실행시킵니다(DB요청이 일어납니다.).
     *      Notice!
     *      POST, PATCH의 경우 jdbcTemplate.update
     *      GET은 대상이 하나일 경우 jdbcTemplate.queryForObject, 대상이 복수일 경우, jdbcTemplate.query 함수를 사용합니다.
     * jdbcTeplate이 실행시킬 때 Query 부분과 Params 부분은 대응(값을 주입)시켜서 DB에 요청합니다.
     * <p>
     * 정리하자면 < 동적 쿼리문 설정(Query) -> 주입될 값 설정(Params) -> jdbcTemplate함수(Query, Params)를 통해 Query, Params를 대응시켜 DB에 요청 > 입니다.
     * <p>
     * <p>
     * DAO관련 함수코드의 후반부는 전반부 코드를 실행시킨 후 어떤 결과값을 반환(return)할 것인지를 결정합니다.
     * 어떠한 값을 반환할 것인지 정의한 후, return문에 전달하면 됩니다.
     * ex) return this.jdbcTemplate.query( ~~~~ ) -> ~~~~쿼리문을 통해 얻은 결과를 반환합니다.
     */

    /**
     * 참고 링크
     * https://jaehoney.tistory.com/34 -> JdbcTemplate 관련 함수에 대한 설명
     * https://velog.io/@seculoper235/RowMapper%EC%97%90-%EB%8C%80%ED%95%B4 -> RowMapper에 대한 설명
     */

    // 회원가입
    public int createOwner(PostOwnerReq postOwnerReq) {
        String createOwnerQuery = "insert into Owner (email, password, nickname, phoneNum) VALUES (?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createOwnerParams = new Object[]{
                postOwnerReq.getEmail(),
                postOwnerReq.getPassword(),
                postOwnerReq.getNickname(),
                postOwnerReq.getPhoneNum()
        }; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createOwnerQuery, createOwnerParams);
        // email -> postOwnerReq.getEmail(), password -> postOwnerReq.getPassword(), nickname -> postOwnerReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 Owner Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        String lastInserIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 OwnerIdx번호를 반환한다.
    }

    // 이메일 확인
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from Owner where email = ?)"; // Owner Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkEmailParams = email; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 회원정보 변경
    public int modifyOwnerName(PatchOwnerReq patchOwnerReq) {
        String modifyOwnerNameQuery = "update Owner set nickname = ? where OwnerIdx = ? "; // 해당 OwnerIdx를 만족하는 Owner를 해당 nickname으로 변경한다.
        Object[] modifyOwnerNameParams = new Object[]{patchOwnerReq.getNickname(), patchOwnerReq.getOwnerIdx()}; // 주입될 값들(nickname, OwnerIdx) 순

        return this.jdbcTemplate.update(modifyOwnerNameQuery, modifyOwnerNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    public int modifyOwnerPhoneNum(PatchOwnerReq patchOwnerReq) {
        String modifyOwnerNameQuery = "update Owner set phoneNum = ? where OwnerIdx = ? "; // 해당 OwnerIdx를 만족하는 Owner의 해당 phoneNum 변경한다.
        Object[] modifyOwnerNameParams = new Object[]{patchOwnerReq.getPhoneNum(), patchOwnerReq.getOwnerIdx()}; // 주입될 값들(phoneNum, OwnerIdx) 순

        return this.jdbcTemplate.update(modifyOwnerNameQuery, modifyOwnerNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 로그인: 해당 email에 해당되는 Owner의 암호화된 비밀번호 값을 가져온다.
    public Owner getPwd(PostLoginReq postLoginReq) throws BaseException {
        String getPwdQuery = "select OwnerIdx, password, email, nickname, phoneNum from Owner where email = ?"; // 해당 email을 만족하는 Owner의 정보들을 조회한다.
        String getPwdParams = postLoginReq.getEmail(); // 주입될 email값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.

        // query에 메일이 존재하지 않는 다른 경우
        // (getPwdQuery에 속성 하나를 빼고 적어 반환값에 포함이 되지 않았으나 rowMapper로 읽어오려고 한다던지 등)
        // 이런 경우에 있을 에러와 이메일이 존재하지 않아서 발생하는 에러는 구분을 해야하는게 맞다.
        // getPwd가 Exception을 throw 하도록 설계 해야함.

        // 다만 위에서 제시된 두개의 다른 문제가 하나의 실행문에 동시 존재한다... 어떻게 구분해야하는가.

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new Owner(
                        rs.getInt("OwnerIdx"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nickname"),
                        rs.getString("phoneNum")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getPwdParams
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // Owner 테이블에 존재하는 전체 유저들의 정보 조회
    public List<GetOwnerRes> getOwners() {
        String getOwnersQuery = "select * from Owner"; //Owner 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getOwnersQuery,
                getOwnerResRowMapper()  // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
    }

    // 해당 nickname을 갖는 유저들의 정보 조회
    public List<GetOwnerRes> getOwnersByNickname(String nickname) {
        String getOwnersByNicknameQuery = "select * from Owner where nickname =?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
        String getOwnersByNicknameParams = nickname;
        return this.jdbcTemplate.query(getOwnersByNicknameQuery,
                getOwnerResRowMapper(), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getOwnersByNicknameParams); // 해당 닉네임을 갖는 모든 Owner 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 OwnerIdx를 갖는 유저조회
    public GetOwnerRes getOwner(int OwnerIdx) {
        String getOwnerQuery = "select * from Owner where OwnerIdx = ?"; // 해당 OwnerIdx를 만족하는 유저를 조회하는 쿼리문
        int getOwnerParams = OwnerIdx;
        return this.jdbcTemplate.queryForObject(getOwnerQuery,
                getOwnerResRowMapper(), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getOwnerParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    private RowMapper<GetOwnerRes> getOwnerResRowMapper(){
        return new RowMapper<GetOwnerRes>() {
            @Override
            public GetOwnerRes mapRow(ResultSet rs, int rowNum) throws SQLException {
                GetOwnerRes getOwnerRes = new GetOwnerRes();
                getOwnerRes.setOwnerIdx(rs.getInt("OwnerIdx"));
                getOwnerRes.setEmail(rs.getString("email"));
                getOwnerRes.setNickname(rs.getString("nickname"));
                getOwnerRes.setPassword(rs.getString("password"));
                getOwnerRes.setPhoneNum(rs.getString("phoneNUm"));
                return getOwnerRes;
            }
        };
    }
}
