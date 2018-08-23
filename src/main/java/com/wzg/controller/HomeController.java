package com.wzg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面跳转控制器
 * @author wuzhigang
 *
 */
@Controller
public class HomeController {
	@RequestMapping("/home")
	public String home(String name) {
		return "home";
	}
}
