package com.nw.spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nw.spring.models.Customer;
import com.nw.spring.models.Pet;
import com.nw.spring.service.imp.CustomerServiceImp;
import com.nw.spring.service.imp.PetServiceImp;

@Controller
public class PetController {

	@Autowired
	private PetServiceImp petService;

	@Autowired
	private CustomerServiceImp customerService;

	@GetMapping("pet/create/{customerId}")
	public String showPetForm(@PathVariable("customerId") long customerId, Map<String, Object> map, Pet pet,
			BindingResult result, Model model) {
		Optional<Customer> customer = customerService.findById(customerId);
		if (customer.isPresent()) {
			List<Pet> pets = petService.findByCustomer(customer.get());
			map.put("title", "Customer And Pets");
			map.put("customer", customer.get());
			map.put("pet", new Pet());
			map.put("pets", pets);
			return "pet/pet_create";
		} else {
			List<Customer> customers = customerService.findAll();
			map.put("title", "Customers");
			map.put("customers", customers);
			map.put("message", "No record found.");
			return "customer/customers";
		}
	}

	@PostMapping("/pet/create/{customerId}")
	public String createPet(@PathVariable("customerId") long customerId, Map<String, Object> map, @Valid Pet pet,
			BindingResult result, Model model) {
		
		Optional<Customer> customer = customerService.findById(customerId);
		if (result.hasErrors()) {
			model.addAttribute("customer", customer.get());
			return "pet/pet_create";
		}
		List<Pet> pets = petService.findByCustomer(customer.get());
		model.addAttribute("pets", pets);
		model.addAttribute("customer", customer.get());
		pet.setCustomer(customer.get());
		petService.save(pet);
		return "redirect:/showPet/{customerId}";
		
		
		/*
		if (!pet.getName().equals("") || !pet.getHealthProblem().equals("")) {
			Optional<Customer> customer = customerService.findById(customerId);
			if (customer.isPresent()) {
				if (result.hasErrors()) {
					map.put("message", "There was a problem.");
				} else {
					pet.setCustomer(customer.get());
					petService.save(pet);
					map.put("message", "Registered successful.");
				}
				List<Pet> pets = petService.findByCustomer(customer.get());
				map.put("customer", customer.get());
				map.put("pet", new Pet());
				map.put("pets", pets);
				map.put("title", "Customer and Pets");
				return "pet/pets";
			} else {
				List<Customer> customers = customerService.findAll();
				map.put("title", "Customers");
				map.put("customers", customers);
				map.put("message", "No record found.");
				return "customer/customers";
			}
		} else {
			Optional<Customer> customer = customerService.findById(customerId);
			if (customer.isPresent()) {
				List<Pet> pets = petService.findByCustomer(customer.get());
				map.put("customer", customer.get());
				map.put("pet", new Pet());
				map.put("message", "Fill in the blank fields.");
				map.put("pets", pets);
				return "pet/pet_create";
			} else {
				List<Customer> customers = customerService.findAll();
				map.put("title", "Customers");
				map.put("customers", customers);
				map.put("message", "Fill in the blank fields.");
				return "customer/customers";
			}
		}
		*/
	}

	@GetMapping("/showPet/{customerId}")
	public String showPetPanel(@PathVariable long customerId, Map<String, Object> map) {
		Optional<Customer> customer = customerService.findById(customerId);
		if (customer.isPresent()) {
			List<Pet> pets = petService.findByCustomer(customer.get());
			map.put("title", "Customer and pets.");
			map.put("customer", customer.get());
			map.put("pets", pets);
			return "pet/pets";
		} else {
			List<Customer> customers = customerService.findAll();
			map.put("title", "Customers");
			map.put("customers", customers);
			map.put("message", "No record found.");
			return "customer/customers";
		}
	}

	@GetMapping("/deletePet/{id}")
	public String deletePetByCustomerId(@PathVariable("id") long id, Map<String, Object> map) {
		//Optional<Customer> customer = customerService.findById(id);
		Optional<Pet> pet = petService.findById(id);
		Customer customer = pet.get().getCustomer();
		if (pet.isPresent()) {
			Boolean control = petService.delete(pet.get(), customer);
			if (control == true) {
				map.put("message", "Deleted successfully.");
			} else {
				map.put("message", "Delete fail.");
			}
		} else {
			map.put("message", "No record found.");
		}
		//List<Customer> customers = customerService.findAll();
		//List<Pet> pets = petService.findAll();
		List<Pet> pets = petService.findByCustomer(customer);
		//Optional<Customer> customer = customerService.findById(id);
		map.put("title", "Customer And Pets");
		map.put("pets", pets);
		map.put("customer", customer);
		return "pet/pets";
	}
	
	@GetMapping("/searchPet/{customerId}")
	public String searchPet(@PathVariable ("customerId") long id, @RequestParam("name") String name, Map<String, Object> map) {
		Optional<Customer> customer = customerService.findById(id);
		List<Pet> mypets = new ArrayList<>();
		if(customer.isPresent()) {
			List<Pet> pets = petService.findByCustomer(customer.get());
			pets.stream().forEach(item -> {
				if(item.getName().toLowerCase().equals(name.toLowerCase())) {
					mypets.add(item);
				}
			});
			if(mypets.size() > 0) {
				map.put("pets", mypets);
				map.put("message", name + " found.");
			} else {
				map.put("pets", pets);
				map.put("message", name + " record not found.");
			}
			map.put("customer", customer.get());
		}
		return "pet/pets";
	}
}
