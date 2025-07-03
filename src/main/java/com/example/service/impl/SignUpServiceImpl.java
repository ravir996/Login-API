package com.example.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.constant.Status;
import com.example.domain.mapper.IUserMapper;
import com.example.domain.vo.UserVO;
import com.example.entity.User;
import com.example.exception.BadDataException;
import com.example.exception.MimicDataException;
import com.example.repo.IUserRepo;
import com.example.service.ISignUpService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements ISignUpService {

	private final IUserRepo userRepo;

	private final PasswordEncoder passwordEncoder;

	private final IUserMapper mapper;

	@Override
	public Mono<UserVO> create(UserVO vo) {
		return checkDuplicateUser(vo).then(Mono.defer(() -> {
			if (!vo.getConfrmPassword().equals(vo.getPassword())) {
				return Mono.error(new BadDataException("Password and Confirm Password do not match."));
			}
			return userRepo.save(toEntity(vo)).map(saved -> toVO(saved, mapper));
		}));
	}

	private Mono<Void> checkDuplicateUser(UserVO vo) {
		return userRepo
				.findByUserNameOrEmailIdOrMobileNoAndStatus(vo.getName(), vo.getEmailId(), vo.getMobileNo(),
						Status.ACTIVE.toString())
				.flatMap(existingUser -> Mono.error(new MimicDataException(
						"User already exists with the given username, email ID, or mobile number.")))
				.then();
	}

	private User toEntity(UserVO vo) {
		User entity = new User();
		entity.setUserName(vo.getName());
		entity.setEmailId(vo.getEmailId());
		entity.setMobileNo(vo.getMobileNo());
		String encryptedPassword = passwordEncoder.encode(vo.getPassword());
		entity.setPassword(encryptedPassword);
		entity.setStatus(Status.ACTIVE.toString());
		return entity;
	}

	private UserVO toVO(User user, IUserMapper mapper) {
		UserVO vo = mapper.toVo(user);
		vo.setMessage("SignUp Successfully");
		return vo;
	}

}
