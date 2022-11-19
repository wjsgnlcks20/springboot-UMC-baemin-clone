package com.example.demo.src.restautrant;

import com.example.demo.config.BaseException;
import com.example.demo.src.menu.MenuProvider;
import com.example.demo.src.menu.MenuService;
import com.example.demo.src.menu.model.DeleteMenuReq;
import com.example.demo.src.menu.model.PatchMenuReq;
import com.example.demo.src.menu.model.PostMenuReq;
import com.example.demo.src.restautrant.model.DeleteRestReq;
import com.example.demo.src.restautrant.model.PatchRestReq;
import com.example.demo.src.restautrant.model.PostRestReq;
import com.example.demo.src.restautrant.model.PostRestRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@Transactional
public class RestaurantService {

    @Autowired
    private final RestaurantProvider restaurantProvider;
    private final RestaurantDao restaurantDao;

    public RestaurantService(RestaurantProvider restaurantProvider, RestaurantDao restaurantDao, MenuProvider menuProvider, MenuService menuService){
        this.restaurantProvider = restaurantProvider;
        this.restaurantDao = restaurantDao;
    }

    // 서비스에서는 가게의 CUD 모두를 담당한다.
    // 메뉴의 CUD는 메뉴 서비스를 사용한다.

    // 생성
    public PostRestRes createRestaurant(PostRestReq postRestReq) throws BaseException {
        try{
            int restaurantIdx = restaurantDao.createRestaurant(postRestReq);
            return new PostRestRes(restaurantIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 수정
    public String modifyRestaurant(PatchRestReq patchRestReq) throws BaseException{
        try{
            int result = 0;
            if(patchRestReq.getName() != null) {
                result += restaurantDao.modifyRestaurantName(patchRestReq);
            }
            else result += 1;
            if(patchRestReq.getCategory() != null) {
                result += restaurantDao.modifyRestaurantCategory(patchRestReq);
            }
            else result += 1;
            if(patchRestReq.getMinimumPaymentCost() != 0) {
                result += restaurantDao.modifyRestaurantMinimumPaymentCost(patchRestReq);
            }
            else result += 1;
            if(patchRestReq.getImageUrl() != null) {
                result += restaurantDao.modifyRestaurantImageUrl(patchRestReq);
            }
            else result += 1;
            if(result < 4) throw new BaseException(MODIFY_FAIL_RESTAURANT);
            return "가게 수정 성공";
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 삭제
    public String deleteRestaurant(DeleteRestReq deleteRestReq) throws BaseException{
        try{
            int result = restaurantDao.deleteRestaurant(deleteRestReq.getRestaurantIdx());
            System.out.println("test11");
            System.out.println(result);
            if(result == 0) throw new BaseException(DELETE_FAIL_RESTAURANT);
            return "가게 삭제 성공";
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
