package com.example.demo.src.restautrant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.Constant;
import com.example.demo.src.menu.MenuProvider;
import com.example.demo.src.menu.MenuService;
import com.example.demo.src.menu.model.*;
import com.example.demo.src.restautrant.model.*;
import com.example.demo.utils.JwtOwnerService;
import com.example.demo.utils.JwtService;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app")
public class RestaurantController{
    @Autowired
    private final RestaurantProvider restaurantProvider;
    private final RestaurantService restaurantService;
    private final JwtOwnerService jwtService;
    private final MenuProvider menuProvider;
    private final MenuService menuService;

    public RestaurantController(RestaurantService restaurantService, RestaurantProvider restaurantProvider, JwtOwnerService jwtService, MenuProvider menuProvider, MenuService menuService){
        this.restaurantProvider = restaurantProvider;
        this.restaurantService = restaurantService;
        this.jwtService = jwtService;
        this.menuProvider = menuProvider;
        this.menuService = menuService;
    }

    // ****************************************
    // 가게

    // 전체 가게 조회(카테고리 파라미터로 같은 카테고리만 검색 가능)
    @GetMapping("/restaurants")
    public BaseResponse<List<GetRestRes>> getRestaurants(@RequestParam(required = false) String category,
                                                         @RequestParam(required = false, defaultValue = "0") Integer startIdx){
        try {
            List<GetRestRes> getRestRes = restaurantProvider.getRestaurants(category);
            if(startIdx != null){
                long cnt = getRestRes.stream().count();
                getRestRes = getRestRes.subList(startIdx, startIdx + Constant.PAGE_CONTENT_LIMIT);
            }
            return new BaseResponse<>(getRestRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    // 특정 가게 조회
    @GetMapping("restaurants/{restaurantIdx}")
    public BaseResponse<GetRestRes> getRestaurant(@PathVariable int restaurantIdx){
        try {
            GetRestRes getRestRes = restaurantProvider.getRestaurant(restaurantIdx);
            return new BaseResponse<>(getRestRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    // 사장님인지 검증하는 jwt 호출 하도 많아 리팩터링함
    private void validateOwnerJWT(int ownerIdx) throws BaseException{
        int ownerIdxByJwt = jwtService.getUserIdx();
        //userIdx와 접근한 유저가 같은지 확인
        //다르다면 접근 권한이 없는 상태로 api 호출
        if(ownerIdx != ownerIdxByJwt){
            throw new BaseException(INVALID_OWNER_JWT);
        }
        Date expirationDate = jwtService.getExpiration();
        Date now = new Date(System.currentTimeMillis());
        if(now.after(expirationDate)){
            throw new BaseException(INVALID_JWT);
        }
    }

    // 가게 등록
    @PostMapping("/owners/{ownerIdx}/restaurants")
    public BaseResponse<PostRestRes> createRestaurant(@PathVariable int ownerIdx, @RequestBody PostRestReq postRestReq){
        try{
            validateOwnerJWT(ownerIdx);
            PostRestRes postRestRes = restaurantService.createRestaurant(postRestReq);
            return new BaseResponse<>(postRestRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 사장님 소유 가게 조회
    @GetMapping("/owners/{ownerIdx}/restaurants")
    public BaseResponse<List<GetRestRes>> getOwnersRestaurants(@PathVariable int ownerIdx){
        try{
            validateOwnerJWT(ownerIdx);
            List<GetRestRes> getRestResList = restaurantProvider.getRestaurantsByOwnerId(ownerIdx);
            return new BaseResponse<>(getRestResList);
        }
        catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/owners/{ownerIdx}/restaurants/{restaurantIdx}")
    public BaseResponse<List<GetRestRes>> getOwnersRestaurant(@PathVariable int ownerIdx, @PathVariable int restaurantIdx){
        try{
            validateOwnerJWT(ownerIdx);
            return new BaseResponse<>(restaurantProvider.getRestaurantsByOwnerId(ownerIdx));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @PatchMapping("/owners/{ownerIdx}/restaurants/{restaurantIdx}")
    public BaseResponse<String> modifyRestaurant(@PathVariable int ownerIdx, @PathVariable int restaurantIdx, @RequestBody PatchRestReq patchRestReq){
        try{
            validateOwnerJWT(ownerIdx);
            return new BaseResponse<>(restaurantService.modifyRestaurant(patchRestReq));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @DeleteMapping("/owners/{ownerIdx}/restaurants/{restaurantIdx}")
    public BaseResponse<String> deleteRestaurant(@PathVariable int ownerIdx, @PathVariable int restaurantIdx, @RequestBody DeleteRestReq deleteRestReq){
        try{
            validateOwnerJWT(ownerIdx);
            return new BaseResponse<>(restaurantService.deleteRestaurant(deleteRestReq));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // ************************************************
    // 메뉴

    // 특정 가게의 모든 메뉴 조회
    @GetMapping("/restaurants/{restaurantIdx}/menus")
    public BaseResponse<List<GetMenuRes>> getRestaurantMenus(@PathVariable int restaurantIdx){
        try{
            return new BaseResponse<>(menuProvider.getMenuInRestaurant(restaurantIdx));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
    // 특정 가게 메뉴 추가 - owner 권한 필요
    @PostMapping("/restaurants/{restaurantIdx}/menus")
    public BaseResponse<PostMenuRes> createRestaurantMenu(@PathVariable int restaurantIdx, @RequestBody PostMenuReq postMenuReqs){
        try{
            int ownerIdx = restaurantProvider.getOwnerIdByRestaurantId(restaurantIdx);
            validateOwnerJWT(ownerIdx);
            return new BaseResponse<>(menuService.createMenu(postMenuReqs));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
    // 특정 가게 특정 메뉴 조회
    @GetMapping("/restaurants/{restaurantIdx}/menus/{menuIdx}")
    public BaseResponse<GetMenuRes> getRestaurantMenu(@PathVariable(required = false) int restaurantIdx, @PathVariable int menuIdx){
        try{
            GetMenuRes getMenuRes = menuProvider.getMenu(menuIdx);
            return new BaseResponse<>(getMenuRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
    // 특정 가게 특정 메뉴 수정 - owner 권한 필요
    @PatchMapping("/restaurants/{restaurantIdx}/menus/{menuIdx}")
    public BaseResponse<String> patchRestaurantMenu(@PathVariable int restaurantIdx, @PathVariable int menuIdx, @RequestBody PatchMenuReq patchMenuReq){
        try{
            // 메뉴는 가게단위에서 API 설계를 진행하였기에 PathVariable로 ownerIdx가 넘어오지 않아
            // 가게 정보를 통해 ownerIdx를 구하여 jwt 인가를 구현.
            int ownerIdx = restaurantProvider.getOwnerIdByRestaurantId(restaurantIdx);
            validateOwnerJWT(ownerIdx);
            return new BaseResponse<>(menuService.modifyMenu(patchMenuReq));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 특정 가게 특정 메뉴 삭제 - owner 권한 필요
    @DeleteMapping("restaurants/{restaurantIdx}/menus/{menuIdx}")
    public BaseResponse<String> deleteRestaurantMenu(@PathVariable int restaurantIdx, @PathVariable int menuIdx, @RequestBody DeleteMenuReq deleteMenuReq){
        try{
            int ownerIdx = restaurantProvider.getOwnerIdByRestaurantId(restaurantIdx);
            validateOwnerJWT(ownerIdx);
            return new BaseResponse<>(menuService.deleteMenu(deleteMenuReq));
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
