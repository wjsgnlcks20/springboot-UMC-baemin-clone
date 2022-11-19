package com.example.demo.src.menu;

import com.example.demo.config.BaseException;
import com.example.demo.src.menu.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class MenuService {
    @Autowired
    private final MenuDao menuDao;
    private final MenuProvider menuProvider;

    public MenuService(MenuDao menuDao, MenuProvider menuProvider) {
        this.menuDao = menuDao;
        this.menuProvider = menuProvider;
    }
    // 생성
    public PostMenuRes createMenu(PostMenuReq postMenuReq) throws BaseException{
        try{
            int menuIdx = menuDao.createMenu(postMenuReq);
            PostMenuRes postMenuRes = new PostMenuRes(menuIdx);
            return postMenuRes;
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 수정
    public String modifyMenu(PatchMenuReq patchMenuReq) throws BaseException{
        try{
            int result = 0;
            if(patchMenuReq.getName() != null) {
                result += menuDao.modifyMenuName(patchMenuReq);
            }
            else result += 1;
            if(patchMenuReq.getPrice() != 0){
                result += menuDao.modifyMenuPrice(patchMenuReq);
            }
            else result += 1;
            if(patchMenuReq.getImageUrl() != null){
                result += menuDao.modifyMenuImageUrl(patchMenuReq);
            }
            else result += 1;
            if(result < 3) {
                throw new BaseException(MODIFY_FAIL_MENU);
            }
            return "메뉴 수정 성공";
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 삭제
    public String deleteMenu(DeleteMenuReq deleteMenuReq) throws BaseException{
        try{
            int result = menuDao.deleteMenu(deleteMenuReq.getMenuIdx());
            if(result == 0) throw new BaseException(DELETE_FAIL_MENU);
            return "메뉴 삭제 성공";
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
