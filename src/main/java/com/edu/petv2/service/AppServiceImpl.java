package com.edu.petv2.service;

import com.edu.petv2.dto.*;
import com.edu.petv2.exception.AccountCreationException;
import com.edu.petv2.exception.BookingCreationException;
import com.edu.petv2.model.*;
import com.edu.petv2.repository.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppServiceImpl implements AppService {

    @Autowired
    private OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;

    @Autowired
    private SitterRepository sitterRepository;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    @Override
    public OwnerDto createOwner(OwnerDto ownerDto) {
        Owner owner = ownerDto.asOwner();
        ownerRepository.save(owner);
        return new OwnerDto(owner);
    }

    public AnimalDto createAnimal(long ownerId,  AnimalDto animalDto){
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        Animal animal = new Animal();
        animal.setOwner(ownerOptional.get());
        animal.setName(animalDto.getName());
        animal.setAge(animalDto.getAge());
        animalRepository.save(animal);
        return new AnimalDto(animal);
    }

    public SitterDto createSitter(SitterDto sitterDto) {
        Sitter sitter = sitterDto.asSitter();
        sitterRepository.save(sitter);
        return new SitterDto(sitter);
    }

    @Override
    public ReviewDto writeReview(long ownerId, ReviewDto reviewDto, long sitterId) {
        Optional<Owner> author = ownerRepository.findById(ownerId);
        Optional<Sitter> sitter = sitterRepository.findById(sitterId);
        Review review = reviewDto.asReview();
        review.setAuthor(author.get());
        review.setReceiver(sitter.get());
        review.setContent(reviewDto.getContent());
        review.setDate(new Date());
        reviewRepository.save(review);
        return new ReviewDto(review);
    }

    @Override
    public BookingDto makeABooking(long ownerId, long sitterId, BookingDto bookingDto) throws BookingCreationException {
        Optional<Owner> owner = ownerRepository.findById(ownerId);
        if (owner.isEmpty()){
            throw new BookingCreationException("Something went wrong regarding the owner");
        }
        Optional<Sitter> sitter = sitterRepository.findById(sitterId);
        if (sitter.isEmpty()){
            throw new BookingCreationException("Something went wrong regarding the sitter");
        }
        Booking booking = bookingDto.asBooking();
        booking.setOwner(owner.get());
        booking.setSitter(sitter.get());
        booking.setStartingDate(bookingDto.getStartingDate());
        booking.setEndingDate(bookingDto.getEndingDate());
        bookingRepository.save(booking);
        return new BookingDto(booking);
    }

    public List<SitterDto> findSitterByPet(String petsAllowed){
        return sitterRepository.getSitterBy(petsAllowed).stream().map(SitterDto::new).collect(Collectors.toList());
    }

    @Override
    public OwnerDto getOwnerById(Long id) {
        return new OwnerDto(ownerRepository.findById(id).get());
    }

    @Override
    public SitterDto getSitterById(Long id) {
        return new SitterDto(sitterRepository.findById(id).get());
    }

    public List<Sitter> getAllSitters(){
        return new ArrayList<>(sitterRepository.findAll());
    }
}
