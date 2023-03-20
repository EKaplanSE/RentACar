package kodlama.io.rentACar.business.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import kodlama.io.rentACar.business.abstracts.BrandService;
import kodlama.io.rentACar.business.requests.CreateBrandRequest;
import kodlama.io.rentACar.business.requests.UpdateBrandRequest;
import kodlama.io.rentACar.business.responses.GetAllBrandsResponse;
import kodlama.io.rentACar.business.responses.GetByIdBrandResponse;
import kodlama.io.rentACar.business.rules.BrandBusinessRules;
import kodlama.io.rentACar.core.utilities.mappers.ModelMapperService;
import kodlama.io.rentACar.core.utilities.results.DataResult;
import kodlama.io.rentACar.core.utilities.results.Result;
import kodlama.io.rentACar.core.utilities.results.SuccessDataResult;
import kodlama.io.rentACar.core.utilities.results.SuccessResult;
import kodlama.io.rentACar.dataAccess.abstracts.BrandRespository;
import kodlama.io.rentACar.entities.concretes.Brand;
import lombok.AllArgsConstructor;

@Service //Bu sınıf bir Business nesnesidir.
@AllArgsConstructor
public class BrandManager implements BrandService{

	private BrandRespository brandRepository;
	private ModelMapperService modelMapperSerive;
	private BrandBusinessRules brandBusinessRules;
	
	@Override
	public DataResult<List<GetAllBrandsResponse>> getAll() {
		
		List<Brand> brands = brandRepository.findAll();
		/*
		 * List<GetAllBrandsResponse> brandsResponse = new
		 * ArrayList<GetAllBrandsResponse>();
		 * 
		 * for (Brand brand : brands) { GetAllBrandsResponse responseItem = new
		 * GetAllBrandsResponse(); responseItem.setId(brand.getId());
		 * responseItem.setName(brand.getName()); brandsResponse.add(responseItem); }
		 */

		List<GetAllBrandsResponse> brandsResponse = brands.stream()
				.map(brand->this.modelMapperSerive.forResponse()
				.map(brand, GetAllBrandsResponse.class))
				.collect(Collectors.toList());
		
		return new SuccessDataResult<List<GetAllBrandsResponse>>(brandsResponse,"Data listelendi") ;
	}

	@Override
	public Result add(CreateBrandRequest createBrandRequest) {
//		Brand brand = new Brand();
//		brand.setName(createBrandRequest.getName());
		
		this.brandBusinessRules.checkIfBrandNameExists(createBrandRequest.getName());
		
		Brand brand = this.modelMapperSerive.forRequest().map(createBrandRequest, Brand.class);
		
		this.brandRepository.save(brand);
		return new SuccessResult("Marka eklendi");
	}

	@Override
	public DataResult<GetByIdBrandResponse> getById(int id) {
		Brand brand = this.brandRepository.findById(id).orElseThrow();
		
		GetByIdBrandResponse response 
		= this.modelMapperSerive.forResponse().map(brand, GetByIdBrandResponse.class);
		return new SuccessDataResult<GetByIdBrandResponse>(response,"Marka getirildi");
	}

	@Override
	public Result update(UpdateBrandRequest updateBrandRequest) {
		this.brandBusinessRules.checkIfBrandNameExists(updateBrandRequest.getName());
		
		Brand brand = this.modelMapperSerive.forRequest().map(updateBrandRequest, Brand.class);
		this.brandRepository.save(brand);
		return new SuccessResult("Marka güncellendi");
	}

	@Override
	public Result delete(int id) {
		this.brandRepository.deleteById(id);
		return new SuccessResult("Marka silindi");
	}

}
