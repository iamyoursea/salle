package com.example.salle.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.salle.application.ProductEditService;
import com.example.salle.application.ProductService;
import com.example.salle.domain.Product;
import com.example.salle.domain.UuidImgname;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ProductEditController {
	
	@Autowired
    ProductService productService;
	
	@Autowired
	ProductEditService productEditService;
	
	@Autowired
	UuidImgname uuidImgname;
    
    //상품등록 이미지파일 업로드
    Product product_file = new Product();
    
    @Value("${cloud.aws.s3.bucket}")
    String bucket; 

	//profile에서 판매글 수정, 삭제하기
	@RequestMapping(value= "/product/{pr_id}/edit", method = RequestMethod.GET)
	public String productEdit(Model model, @PathVariable int pr_id) {
		List<String> imgList = new ArrayList<String>();
		Product product = productEditService.productEdit(pr_id, imgList);
		model.addAttribute("product", product);
		model.addAttribute("imgList", imgList);
		imgList = null;
		return "product/productEdit"; 
	}
	

    @RequestMapping(value= "/productEditImg/ajax", method= RequestMethod.POST)
    public void productEditImg(@RequestBody String json) throws Exception {
    	productEditService.imgEdit(json, productTemp, bucket);
    }
	
    
	Product productTemp;
	@RequestMapping(value= "/product/{pr_id}/save", method= RequestMethod.POST)
	public String profileEditDone(@ModelAttribute("product") Product product, Errors errors,
			HttpSession httpSession, @PathVariable("pr_id") int pr_id) {
		productEditService.productSave(product, productTemp, httpSession, errors);
		
		if (errors.hasErrors())
			return "product/productEdit";
		
		productService.updateProduct(product);
		return "product/productInfo";
	} 
    	
	
	String flag;	
	@RequestMapping(value= "/product/{pr_id}/delete", method= RequestMethod.GET)
	public String profileDelete(Model model, @PathVariable int pr_id) throws UnsupportedEncodingException {	
		if (flag.equals("true"))
			productService.deleteProduct(pr_id);
		
		String nickName = productEditService.productDelete(pr_id);
		return "redirect:/profile/" + nickName;
	}
	
	
	@RequestMapping(value= "/productDelete/ajax", method=RequestMethod.POST)
	public void ajaxDelete(@RequestBody String json) {
		JSONObject jsn = new JSONObject(json);
		flag = (String) jsn.get("flag");
	}
}
