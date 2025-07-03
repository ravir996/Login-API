package com.example.domain.mapper;



import org.mapstruct.Mapper;

import com.example.domain.vo.UserVO;
import com.example.entity.User;

@Mapper(componentModel = "spring")
public interface IUserMapper extends BaseMapper<User, UserVO>{
	
	public User toEntity(UserVO vo);

	public UserVO toVo(User entity);

}
