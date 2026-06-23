# Backend Development Standards — Spring Boot 4.1.0 + MyBatis-Plus

## Project Structure

```
src/main/java/com/example/{module}/
├── controller/    # REST controllers
├── service/       # Service interfaces
│   └── impl/      # Service implementations
├── mapper/        # MyBatis-Plus mappers
├── entity/        # Database entities (PO)
├── dto/           # Data Transfer Objects
├── vo/            # View Objects (response)
├── config/        # Spring configuration
├── common/        # Shared utilities, constants, exceptions
│   ├── Result.java           # Unified API response wrapper
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
└── Application.java

src/main/resources/
├── application.yml
├── application-dev.yml
├── application-test.yml
└── application-prod.yml
```

## Unified Response Pattern

```java
public record Result<T>(
    int code,
    String message,
    T data
) {
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
}
```

## Controller Template

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateDTO dto) {
        return Result.ok(userService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return Result.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok(null);
    }

    @GetMapping
    public Result<Page<UserVO>> page(UserPageQuery query) {
        return Result.ok(userService.page(query));
    }
}
```

## Service Template

```java
public interface UserService extends IService<User> {
    UserVO getById(Long id);
    UserVO create(UserCreateDTO dto);
    UserVO update(Long id, UserUpdateDTO dto);
    void delete(Long id);
    Page<UserVO> page(UserPageQuery query);
}

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public UserVO getById(Long id) {
        User user = getByIdOrThrow(id);
        return UserConverter.INSTANCE.toVO(user);
    }

    @Override
    @Transactional
    public UserVO create(UserCreateDTO dto) {
        User user = UserConverter.INSTANCE.toEntity(dto);
        save(user);
        return UserConverter.INSTANCE.toVO(user);
    }

    @Override
    @Transactional
    public UserVO update(Long id, UserUpdateDTO dto) {
        User user = getByIdOrThrow(id);
        UserConverter.INSTANCE.update(dto, user);
        updateById(user);
        return UserConverter.INSTANCE.toVO(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = getByIdOrThrow(id);
        removeById(user.getId()); // logical delete if configured
    }

    private User getByIdOrThrow(Long id) {
        return Optional.ofNullable(getById(id))
            .orElseThrow(() -> new BusinessException(404, "User not found: " + id));
    }
}
```

## Entity Template

```java
@Data
@TableName("user")
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String email;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
```

## Mapper Template

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // Custom queries via XML or annotation
    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);
}
```

## Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return Result.fail(400, msg);
    }
}
```

## Testing Standards

- **Unit tests**: Test every Service method with mocked dependencies
- **Integration tests**: Test every Controller endpoint with `@SpringBootTest` and `@AutoConfigureMockMvc`
- **Coverage target**: ≥ 80% line coverage
- **Naming**: `{ClassName}Test` for unit, `{ClassName}IT` for integration

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserMapper userMapper;
    @InjectMocks private UserServiceImpl userService;

    @Test
    void shouldReturnUserWhenExists() {
        when(userMapper.selectById(1L)).thenReturn(mockUser());
        UserVO result = userService.getById(1L);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> userService.getById(999L));
    }
}
```
