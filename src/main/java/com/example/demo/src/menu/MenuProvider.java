package com.example.demo.src.menu;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.menu.model.GetMenuRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class MenuProvider {
    @Autowired
    private final MenuDao menuDao;

    public MenuProvider(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    public List<GetMenuRes> getMenuInRestaurant(int restaurantIdx) throws BaseException{
        try{
            List<GetMenuRes> getMenuResList = menuDao.getMenuByRestaurantId(restaurantIdx);
            return getMenuResList;
        }
        catch(Exception exception){
            try{
                return menuDao.getAllMenu();
            }
            catch(Exception exception1){
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public GetMenuRes getMenu(int menuIdx) throws BaseException{
        try{
            GetMenuRes getMenuRes = menuDao.getMenu(menuIdx);
            return getMenuRes;
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
