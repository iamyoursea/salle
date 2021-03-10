package com.example.salle.application;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.salle.domain.Login;
import com.example.salle.domain.Product;
import com.example.salle.domain.UuidImgname;
import com.example.salle.mapper.ProductMapper;
import com.example.salle.validation.SellProductValidation;

@Transactional
@Service
public class ProductService implements ProductMapper {
	
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	
    @Autowired
    ProductMapper productMapper;
    
	@Autowired
	UuidImgname uuidImg;
	
	@Autowired
	AmazonS3Service amazonS3;
      
    Timestamp productRegTime;
    
	@Override
	public void registerProduct(HttpSession httpSession, Product product, Product product_file, Errors errors) {	
		log.info("pr_img_1" + product_file.getPr_img_1());
    	Login loginInfo = (Login) httpSession.getAttribute("login");
    	product.setPr_email(loginInfo.getEmail());    	
		product.setPr_img_1(product_file.getPr_img_1());
		product.setPr_img_2(product_file.getPr_img_2());
		product.setPr_img_3(product_file.getPr_img_3());
		product.setPr_img_4(product_file.getPr_img_4());
		product.setPr_img_5(product_file.getPr_img_5());
		product.setPr_title_alias(product.getPr_title().replaceAll("\\s", ""));
    	
		new SellProductValidation().validate(product, errors);

		if (!errors.hasErrors()) {	
			productRegTime = Timestamp.valueOf(LocalDateTime.now());        
			product.setPr_reg_date(productRegTime);
			insertProduct(product);
		}
	
		product_file.setPr_img_1(null);
		product_file.setPr_img_2(null);
		product_file.setPr_img_3(null);
		product_file.setPr_img_4(null);
		product_file.setPr_img_5(null);
	}
	
	
	@Override
	public void insertImg(HttpServletRequest req, Product product_file, String bucket) throws IOException {
		log.info("insertImg in processing");
    	MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) req;
    	Iterator<String> iterator = multiReq.getFileNames(); 	
    	MultipartFile multipartFile = null;
    	
    	int reps = 0;
    	while(iterator.hasNext()) {
    		
    		multipartFile = multiReq.getFile(iterator.next());
    		String fileOriname = multipartFile.getOriginalFilename();
    		String ranCode = uuidImg.makeFilename(fileOriname);
    		String dirName = "static/img";
    		String fileName = dirName + "/" + ranCode;
    		
    		switch(reps) {
    		case 0: 
    			product_file.setPr_img_1(fileName);
    			break;
    		case 1: 
    			product_file.setPr_img_2(fileName);
    			break;
    		case 2: 
    			product_file.setPr_img_3(fileName);
    			break;
    		case 3: 
    			product_file.setPr_img_4(fileName);
    			break;
    		case 4: 
    			product_file.setPr_img_5(fileName);
    			break;
    		}
    		reps++;
    		amazonS3.uploadImg(bucket, fileName, multipartFile);
    	}
	}

	@Override
	public int getCountProduct() {
		return productMapper.getCountProduct();
	}

	@Override
	public void deleteProduct(int pr_id) {
		productMapper.deleteProduct(pr_id);
	}

	@Override
	public List<Product> getProductList() {
		return productMapper.getProductList();
	}

	@Override
	public List<Product> getCategoryProductList(String pr_category) {
		return productMapper.getCategoryProductList(pr_category);
	}

	@Override
	public Product getProductInfo(int pr_id) {
		return productMapper.getProductInfo(pr_id);
	}

	@Override
	public String getMemberProductInfo(String email) {
		return productMapper.getMemberProductInfo(email);
	}

	@Override
	public List<Product> search(String searchWord, String searchWordNoSpace) { 
		return productMapper.search(searchWord, searchWordNoSpace);
	}

	@Override
	public int searchCount(String searchWord, String searchWordNoSpace) {
		return productMapper.searchCount(searchWord, searchWordNoSpace);
	}

	@Override
	public void updateProduct(Product product) {
		productMapper.updateProduct(product);
	}

	@Override
	public void deleteImg1(int pr_id) {
		productMapper.deleteImg1(pr_id);
	}

	@Override
	public void deleteImg2(int pr_id) {
		productMapper.deleteImg2(pr_id);
	}

	@Override
	public void deleteImg3(int pr_id) {
		productMapper.deleteImg3(pr_id);
	}

	@Override
	public void deleteImg4(int pr_id) {
		productMapper.deleteImg4(pr_id);
	}

	@Override
	public void deleteImg5(int pr_id) {
		productMapper.deleteImg5(pr_id);
	}

	@Override
	public void insertProduct(Product product) {	
		productMapper.insertProduct(product);
	}

}
