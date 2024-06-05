package com.erudio.services;

import com.erudio.controllers.PersonController;
import com.erudio.data.vo.v1.PersonVO;
import com.erudio.exceptions.RequiredObjectIsNullException;
import com.erudio.mapper.DozerMapper;
import com.erudio.controllers.BookController;
import com.erudio.data.vo.v1.BookVO;
import com.erudio.exceptions.ResourceNotFoundException;
import com.erudio.model.Book;
import com.erudio.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookServices {

    private Logger logger = Logger.getLogger(BookServices.class.getName());

    @Autowired
    BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookVO> assembler;


    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
        logger.info("Finding all book");

        var bookPage = repository.findAll(pageable);

        var bookVosPage = bookPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
        bookVosPage.map(
                p -> p.add(
                        linkTo(methodOn(BookController.class)
                                .findById(p.getKey())).withSelfRel()));
        Link link = linkTo(methodOn(BookController.class).findAll(
                pageable.getPageNumber(), bookPage.getSize(), "asc")).withSelfRel();

        return assembler.toModel(bookVosPage, link);

    }

    public BookVO findById(Long id) {
        logger.info("Finding one book");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
        var vo = DozerMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return vo;
    }

    public BookVO create(BookVO book) {
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one book!");
        var entity = DozerMapper.parseObject(book, Book.class);
        var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    /*public BookVOV2 createV2(BookVOV2 book) {

        logger.info("Creating one book with V2!");
        var entity = mapper.convertVoToEntity(book);
        var vo =  mapper.convertEntityToVo(repository.save(entity));
        return vo;
    }*/

    public BookVO update(BookVO book) {
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one book.");

        var entity = repository.findById(book.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));


        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var vo =  DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting one book.");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));

        repository.delete(entity);
    }


}
