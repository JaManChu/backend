package com.recipe.jamanchu.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.recipe.jamanchu.auth.jwt.JwtUtil;
import com.recipe.jamanchu.auth.oauth2.CustomOauth2UserService;
import com.recipe.jamanchu.auth.oauth2.KakaoUserDetails;
import com.recipe.jamanchu.component.UserAccessHandler;
import com.recipe.jamanchu.entity.RecipeEntity;
import com.recipe.jamanchu.entity.UserEntity;
import com.recipe.jamanchu.exceptions.exception.AccessTokenRetrievalException;
import com.recipe.jamanchu.exceptions.exception.PasswordMismatchException;
import com.recipe.jamanchu.exceptions.exception.SocialAccountException;
import com.recipe.jamanchu.exceptions.exception.UserNotFoundException;
import com.recipe.jamanchu.model.dto.request.auth.LoginDTO;
import com.recipe.jamanchu.model.dto.request.auth.SignupDTO;
import com.recipe.jamanchu.model.dto.request.auth.UserUpdateDTO;
import com.recipe.jamanchu.model.dto.response.ResultResponse;
import com.recipe.jamanchu.model.dto.response.auth.UserInfoDTO;
import com.recipe.jamanchu.model.dto.response.mypage.MyRecipeInfo;
import com.recipe.jamanchu.model.dto.response.mypage.MyRecipes;
import com.recipe.jamanchu.model.dto.response.mypage.MyScrapedRecipes;
import com.recipe.jamanchu.model.dto.response.mypage.PageResponse;
import com.recipe.jamanchu.model.type.CookingTimeType;
import com.recipe.jamanchu.model.type.LevelType;
import com.recipe.jamanchu.model.type.RecipeProvider;
import com.recipe.jamanchu.model.type.ResultCode;
import com.recipe.jamanchu.model.type.ScrapedType;
import com.recipe.jamanchu.model.type.TokenType;
import com.recipe.jamanchu.model.type.UserRole;
import com.recipe.jamanchu.repository.RecipeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserAccessHandler userAccessHandler;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private CustomOauth2UserService customOauth2UserService;

  @Mock
  private RecipeRepository recipeRepository;

  @InjectMocks
  private UserServiceImpl userServiceimpl;

  private static final Long USERID = 1L;
  private static final String EMAIL = "test@email.com";
  private static final String NICKNAME = "nickname";
  private static final String PASSWORD = "1234";
  private static final String PROVIDER = "kakao";
  private static final String BEFORE_PASSWORD = "oldPassword";
  private static final String AFTER_PASSWORD = "newPassword";
  private static final String NEW_NICKNAME = "newNickName";
  private static final String ACCESS = TokenType.ACCESS.getValue();
  private static final String REFRESH = "refresh-token";
  private static final String CODE = "kakaoCode";
  private static final String KAKAO_ACCESS_TOKEN = "kakaoAccessToken";
  private static final String REDIRECT_URI = "https://frontend-dun-eight-78.vercel.app/users/login/auth/kakao";
  private static final int MYRECIPEID = 1;
  private static final int MYSCRAPEDRECIPEID = 1;


  private SignupDTO signup;
  private UserUpdateDTO userUpdateDTO;
  private UserEntity user;
  private UserEntity kakaoUser;
  private UserInfoDTO userInfoDTO;
  private LoginDTO loginDTO;
  private KakaoUserDetails kakaoUserDetails;
  private List<RecipeEntity> myRecipeList;
  private List<RecipeEntity> myScrapRecipeList;
  private List<MyRecipes> myRecipes;
  private List<MyScrapedRecipes> myScrapedRecipes;


  @BeforeEach
  void setUp() {
    signup = new SignupDTO(EMAIL, PASSWORD, NICKNAME);
    userUpdateDTO = new UserUpdateDTO(NEW_NICKNAME, PASSWORD);
    userInfoDTO = new UserInfoDTO(EMAIL, NICKNAME);
    loginDTO = new LoginDTO(EMAIL, PASSWORD);

    // 일반 회원
    user = UserEntity.builder()
        .userId(1L)
        .email(EMAIL)
        .nickname(NICKNAME)
        .role(UserRole.USER)
        .password(PASSWORD)
        .provider(null)
        .build();

    // 카카오로 로그인한 회원
    kakaoUser = UserEntity.builder()
        .userId(1L)
        .email(EMAIL)
        .nickname(NICKNAME)
        .role(UserRole.USER)
        .password(PASSWORD)
        .provider(PROVIDER)
        .build();

    Map<String, Object> kakaoAccount = new HashMap<>();
    kakaoAccount.put("email", EMAIL);

    Map<String, Object> properties = new HashMap<>();
    properties.put("nickname", NICKNAME);

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", "providerId");
    attributes.put("kakao_account", kakaoAccount);
    attributes.put("properties", properties);

    kakaoUserDetails = new KakaoUserDetails(attributes);

    myRecipeList = List.of(
        new RecipeEntity(
            1L, user, "레시피 1", LevelType.LOW, CookingTimeType.TEN_MINUTES,
            "thumbnail 1", RecipeProvider.USER, null, null, null, null, null, null),
        new RecipeEntity(
            2L, user, "레시피 2", LevelType.MEDIUM, CookingTimeType.FIFTEEN_MINUTES,
            "thumbnail 2", RecipeProvider.USER, null, null, null, null, null, null)
    );

    myScrapRecipeList = List.of(
        new RecipeEntity(
            3L, kakaoUser, "레시피 3", LevelType.LOW, CookingTimeType.TEN_MINUTES,
            "thumbnail 3", RecipeProvider.USER, null, null, null, null, null, null),
        new RecipeEntity(
            4L, kakaoUser, "레시피 4", LevelType.MEDIUM, CookingTimeType.FIFTEEN_MINUTES,
            "thumbnail 4", RecipeProvider.USER, null, null, null, null, null, null)
    );

    myRecipes = myRecipeList.stream()
        .limit(20)
        .map(recipe -> new MyRecipes(
            recipe.getId(),
            recipe.getName(),
            recipe.getThumbnail()
        )).toList();

    myScrapedRecipes = myScrapRecipeList.stream()
        .limit(20)
        .map(scraped -> new MyScrapedRecipes(
            scraped.getId(),
            scraped.getName(),
            scraped.getUser().getNickname(),
            scraped.getThumbnail()
        )).toList();
  }

  @Test
  @DisplayName("회원가입 성공")
  void signup_Success() {

    // when
    ResultResponse result = userServiceimpl.signup(signup);

    // then
    assertEquals(ResultCode.SUCCESS_SIGNUP.getStatusCode(), result.getCode());
  }

  @Test
  @DisplayName("로그인 성공")
  void login_Success() {
    // given
    when(userAccessHandler.findByEmail(loginDTO.getEmail())).thenReturn(user);
    doNothing().when(userAccessHandler).validatePassword(user.getPassword(), loginDTO.getPassword());
    when(jwtUtil.createJwt("access", user.getUserId(), user.getRole())).thenReturn(ACCESS);
    when(jwtUtil.createJwt("refresh", user.getUserId(), user.getRole())).thenReturn(REFRESH);

    // when
    ResultResponse resultResponse = userServiceimpl.login(loginDTO, response);

    assertEquals(resultResponse.getCode(), HttpStatus.OK);
    assertEquals(resultResponse.getData(), user.getNickname());
  }

  @Test
  @DisplayName("로그인 실패 : 존재하지 않은 사용자")
  void login_UserNotFound() {
    // given
    when(userAccessHandler.findByEmail(loginDTO.getEmail())).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.login(loginDTO, response));
  }

  @Test
  @DisplayName("로그인 실패 : 비밀번호 불일치")
  void login_PasswordMisMatch() {
    // given
    loginDTO = new LoginDTO(EMAIL, BEFORE_PASSWORD);
    when(userAccessHandler.findByEmail(loginDTO.getEmail())).thenReturn(user);

    doThrow(new PasswordMismatchException()).when(userAccessHandler)
        .validatePassword(user.getPassword(), loginDTO.getPassword());

    // when then
    assertThrows(PasswordMismatchException.class,
        () -> userServiceimpl.login(loginDTO, response));
  }

  @Test
  @DisplayName("카카오 로그인 : 성공")
  void kakaoLogin_Success() {
    // given
    when(customOauth2UserService.getAccessToken(CODE)).thenReturn(KAKAO_ACCESS_TOKEN);
    when(customOauth2UserService.getUserDetails(KAKAO_ACCESS_TOKEN)).thenReturn(kakaoUserDetails);
    when(userAccessHandler.findOrCreateUser(kakaoUserDetails)).thenReturn(user);
    when(jwtUtil.createJwt("access", user.getUserId(), user.getRole())).thenReturn(ACCESS);
    when(jwtUtil.createJwt("refresh", user.getUserId(), user.getRole())).thenReturn(REFRESH);

    // when
    String resultResponse = userServiceimpl.kakaoLogin(CODE, response);

    // then
    String response = UriComponentsBuilder.fromUriString(REDIRECT_URI)
        .queryParam(TokenType.ACCESS.getValue(), ACCESS)
        .queryParam("nickname", user.getNickname())
        .queryParam("provider", user.getProvider())
        .build()
        .toUriString();

    assertEquals(response, resultResponse);
  }

  @Test
  @DisplayName("카카오 로그인 실패: 엑세스 토큰 발급 실패")
  void kakaoLogin_AccessDenied() {
    // given
    when(customOauth2UserService.getAccessToken(CODE)).thenThrow(new AccessTokenRetrievalException());

    // when & then
    assertThrows(RuntimeException.class, () -> userServiceimpl.kakaoLogin(CODE, response));
  }

  @Test
  @DisplayName("카카오 로그인 실패 : 사용자 정보 가져오기 실패")
  void kakaoLogin_KakaoAccessDenied() {
    when(customOauth2UserService.getAccessToken(CODE)).thenReturn(KAKAO_ACCESS_TOKEN);
    when(customOauth2UserService.getUserDetails(KAKAO_ACCESS_TOKEN)).thenThrow(new UserNotFoundException());

    // when & then
    assertThrows(RuntimeException.class, () -> userServiceimpl.kakaoLogin(CODE, response));

  }

  @Test
  @DisplayName("회원정보 수정 성공 - 닉네임, 패스워드 모두 변경")
  void updateUserInfo_SuccessForPasswordAndNickname() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    doNothing().when(userAccessHandler).isSocialUser(user.getProvider());

    // when
    ResultResponse result = userServiceimpl.updateUserInfo(request, userUpdateDTO);

    // then
    assertEquals(ResultCode.SUCCESS_UPDATE_USER_INFO.getStatusCode(), result.getCode());
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 존재하지 않은 회원인 경우")
  void updateUserInfo_NotFoundUser() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.updateUserInfo(request, userUpdateDTO));
  }

  @Test
  @DisplayName("회원정보 수정 실패 : 카카오로 로그인을 한 회원")
  void updateUserInfo_SocialAccountException() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(kakaoUser);

    doThrow(new SocialAccountException()).when(userAccessHandler).isSocialUser(kakaoUser.getProvider());

    // when then
    assertThrows(SocialAccountException.class,
        () -> userServiceimpl.updateUserInfo(request, userUpdateDTO));
  }

  @Test
  @DisplayName("회원 탈퇴 성공 : 일반 회원")
  void deleteUser_Success() {

    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    // when
    ResultResponse result = userServiceimpl.deleteUser(request);

    // then
    assertEquals(ResultCode.SUCCESS_DELETE_USER.getStatusCode(), result.getCode());
  }

  @Test
  @DisplayName("회원 탈퇴 성공 : 소셜 가입을 한 회원")
  void deleteUser_Success_SocialAccount() {

    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(kakaoUser);

    // when
    ResultResponse result = userServiceimpl.deleteUser(request);

    // then
    assertEquals(ResultCode.SUCCESS_DELETE_USER.getStatusCode(), result.getCode());
  }

  @Test
  @DisplayName("회원 탈퇴 실패 : 존재하지 않은 회원인 경우")
  void deleteUser_NotFoundUser() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.deleteUser(request));
  }


  @Test
  @DisplayName("회원 정보 조회 성공")
  void getUserInfo_Success() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);

    // when
    ResultResponse response = userServiceimpl.getUserInfo(request);

    // then
    assertEquals(ResultCode.SUCCESS_GET_USER_INFO.getStatusCode(), response.getCode());
    assertEquals(userInfoDTO.getEmail(), ((UserInfoDTO)response.getData()).getEmail());
    assertEquals(userInfoDTO.getNickname(), ((UserInfoDTO)response.getData()).getNickname());
  }

  @Test
  @DisplayName("회원 정보 조회 실패 : 존재하지 않은 사용자")
  void getUserInfo_NotFoundUser() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.getUserInfo(request));
  }

  @Test
  @DisplayName("마이페이지 레시피 정보 조회 성공")
  void getUserRecipeInfo_Success() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenReturn(user);
    when(recipeRepository.findAllByUser(user)).thenReturn(Optional.of(myRecipeList));
    when(recipeRepository.findScrapRecipeByUser(user, ScrapedType.SCRAPED)).thenReturn(Optional.of(myScrapRecipeList));

    PageResponse<MyRecipes> myRecipesPage = PageResponse.pagination(myRecipes, MYRECIPEID);
    PageResponse<MyScrapedRecipes> myScrapedRecipesPage = PageResponse.pagination(myScrapedRecipes, MYSCRAPEDRECIPEID);

    ResultResponse expectedResponse = new ResultResponse(ResultCode.SUCCESS_GET_USER_RECIPES_INFO,
        new MyRecipeInfo(myRecipesPage, myScrapedRecipesPage)
    );

    // when
    ResultResponse actualResponse = userServiceimpl.getUserRecipes(MYRECIPEID, MYSCRAPEDRECIPEID, request);

    MyRecipeInfo expectedRecipeInfo = (MyRecipeInfo) expectedResponse.getData();
    MyRecipeInfo actualRecipeInfo = (MyRecipeInfo) actualResponse.getData();

    // then
    assertEquals(expectedResponse.getCode(), actualResponse.getCode());

    assertEquals(expectedRecipeInfo.getMyRecipes().getTotalPage(), actualRecipeInfo.getMyRecipes().getTotalPage());
    assertEquals(expectedRecipeInfo.getMyScrapedRecipes().getTotalPage(), actualRecipeInfo.getMyScrapedRecipes().getTotalPage());

    assertEquals(expectedRecipeInfo.getMyRecipes().getTotalData(), actualRecipeInfo.getMyRecipes().getTotalData());
    assertEquals(expectedRecipeInfo.getMyScrapedRecipes().getTotalData(), actualRecipeInfo.getMyScrapedRecipes().getTotalData());

    for (int i = 0; i < expectedRecipeInfo.getMyRecipes().getDataList().size(); i++) {
      MyRecipes expectedMyRecipe = expectedRecipeInfo.getMyRecipes().getDataList().get(i);
      MyRecipes actualMyRecipe = actualRecipeInfo.getMyRecipes().getDataList().get(i);
      assertEquals(expectedMyRecipe.getMyRecipeId(), actualMyRecipe.getMyRecipeId());
      assertEquals(expectedMyRecipe.getMyRecipeName(), actualMyRecipe.getMyRecipeName());
      assertEquals(expectedMyRecipe.getMyRecipeThumbnail(), actualMyRecipe.getMyRecipeThumbnail());
    }

    for (int i = 0; i < expectedRecipeInfo.getMyScrapedRecipes().getDataList().size(); i++) {
      MyScrapedRecipes expectedScrapedRecipe = expectedRecipeInfo.getMyScrapedRecipes().getDataList().get(i);
      MyScrapedRecipes actualScrapedRecipe = actualRecipeInfo.getMyScrapedRecipes().getDataList().get(i);
      assertEquals(expectedScrapedRecipe.getRecipeId(), actualScrapedRecipe.getRecipeId());
      assertEquals(expectedScrapedRecipe.getRecipeName(), actualScrapedRecipe.getRecipeName());
      assertEquals(expectedScrapedRecipe.getRecipeAuthor(), actualScrapedRecipe.getRecipeAuthor());
      assertEquals(expectedScrapedRecipe.getRecipeThumbnail(), actualScrapedRecipe.getRecipeThumbnail());
    }
  }

  @Test
  @DisplayName("마이페이지 레시피 정보 조회 실패 : 사용자 없음")
  void getUserRecipeInfo_UserNotFound() {
    // given
    when(jwtUtil.getUserId(request.getHeader(TokenType.ACCESS.getValue()))).thenReturn(USERID);
    when(userAccessHandler.findByUserId(USERID)).thenThrow(new UserNotFoundException());

    // when then
    assertThrows(UserNotFoundException.class,
        () -> userServiceimpl.getUserRecipes(MYRECIPEID, MYSCRAPEDRECIPEID, request));
  }
}



