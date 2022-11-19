package com.example.demo.src.restautrant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.menu.MenuProvider;
import com.example.demo.src.menu.model.GetMenuRes;
import com.example.demo.src.restautrant.model.GetRestRes;
import com.example.demo.src.restautrant.model.Restaurant;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class RestaurantProvider {

    @Autowired
    private final RestaurantDao restaurantDao;

    public RestaurantProvider(RestaurantDao restaurantDao){
        this.restaurantDao = restaurantDao;
    }
    // ******************************************************
    // 가게 정보

    // 모든 가게 (category가 있다면 그것에 따라) 의 정보를 전달
    public List<GetRestRes> getRestaurants(String category) throws BaseException {
        // 카테고리를 입력받는다면 카테고리에 해당하는 가게들만 찾아 반환한다.
        try {
            if (category != null) {
                return restaurantDao.getRestaurantByCategory(category);
            }
            return restaurantDao.getRestaurants();
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 특정 가게의 정보를 전달
    public GetRestRes getRestaurant(int restaurantIdx) throws BaseException {
        try {
            return restaurantDao.getRestaurant(restaurantIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetRestRes> getRestaurantsByOwnerId(int ownerIdx) throws BaseException{
        try{
            return restaurantDao.getRestaurantsByOwnerId(ownerIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getOwnerIdByRestaurantId(int restaurantIdx) throws BaseException{
        try{
            return restaurantDao.getOwnerIdByRestaurantId(restaurantIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
