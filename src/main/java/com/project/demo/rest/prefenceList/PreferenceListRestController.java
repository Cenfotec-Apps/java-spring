package com.project.demo.rest.prefenceList;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.movie.Movie;
import com.project.demo.logic.entity.movie.MovieRepository;
import com.project.demo.logic.entity.preferenceList.PreferenceList;
import com.project.demo.logic.entity.preferenceList.PreferenceListRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/preferenceList")
public class PreferenceListRestController {

    @Autowired
    private PreferenceListRepository preferenceListRepository;

    @Autowired
    private MovieRepository movieRepository;

    // GET - POST - PATCH - PUT - DELETE

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPreferenceLists(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<PreferenceList> preferenceListsPage = preferenceListRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(preferenceListsPage.getTotalPages());
        meta.setTotalElements(preferenceListsPage.getTotalElements());
        meta.setPageNumber(preferenceListsPage.getNumber() + 1);
        meta.setPageSize(preferenceListsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Preference Lists retrieved successfully",
                preferenceListsPage.getContent(), HttpStatus.OK, meta);

    }

    @GetMapping("/{Id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPrefenceListById(@PathVariable Long Id, HttpServletRequest request) {
        Optional<PreferenceList> foundPrefereceList = preferenceListRepository.findById(Id);
        if(foundPrefereceList.isPresent())  {
            return new GlobalResponseHandler().handleResponse(
                    "Preference List retrieved successfully",
                    foundPrefereceList.get(),
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Preference List with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addPreferenceList(@RequestBody PreferenceList preferenceList, HttpServletRequest request) {
        PreferenceList savedPreferencelist = preferenceListRepository.save(preferenceList);
        return new GlobalResponseHandler().handleResponse(
                "Preference List successfully saved",
                savedPreferencelist,
                HttpStatus.OK,
                request);
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePreferenceList(@RequestBody PreferenceList preferenceList, HttpServletRequest request) {
        PreferenceList savedPreferencelist = preferenceListRepository.save(preferenceList);
        return new GlobalResponseHandler().handleResponse(
                "Preference List successfully updated",
                savedPreferencelist,
                HttpStatus.OK,
                request);
    }

    @DeleteMapping("/{Id}")
    @PreAuthorize("isAuthenticated() && hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deletePreferenceList(@PathVariable Long Id, HttpServletRequest request) {
        Optional<PreferenceList> foundPrefereceList = preferenceListRepository.findById(Id);
        if(foundPrefereceList.isPresent()) {
            preferenceListRepository.deleteById(Id);
            return new GlobalResponseHandler().handleResponse(
                    "Preference List successfully deleted",
                    HttpStatus.OK,
                    request);
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Preference List with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @PatchMapping("/{Id}/addMovie")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addMovieToPreferenceLis(@PathVariable Long Id, @RequestBody Movie movie, HttpServletRequest request ) {
        Optional<PreferenceList> foundPrefereceList = preferenceListRepository.findById(Id);
        if(foundPrefereceList.isPresent()) {
            Movie toSaveMovie = new Movie();
            if(movie.getId() != null) {
                Optional<Movie> foundMovie = movieRepository.findById(movie.getId());
                if(foundMovie.isPresent()) {
                    toSaveMovie = foundMovie.get();
                }
            } else {
                toSaveMovie = movie;
            }
            if(foundPrefereceList.get().getMovies().contains(toSaveMovie)) {
                return new GlobalResponseHandler().handleResponse(
                        "Movie already in the preference list",
                        HttpStatus.BAD_REQUEST,
                        request);
            } else {
                foundPrefereceList.get().getMovies().add(toSaveMovie);
                preferenceListRepository.save(foundPrefereceList.get());
                return new GlobalResponseHandler().handleResponse(
                        "Movie added to the list",
                        foundPrefereceList.get(),
                        HttpStatus.OK,
                        request);

            }
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Preference List with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @PatchMapping("/{Id}/removeMovie")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> rmMovieToPreferenceLis(@PathVariable Long Id, @RequestBody Movie movie, HttpServletRequest request ) {
        Optional<PreferenceList> foundPrefereceList = preferenceListRepository.findById(Id);
        if(foundPrefereceList.isPresent()) {
            Movie toRemoveMovie = new Movie();
            if(movie.getId() == null) {
                return new GlobalResponseHandler().handleResponse(
                        "Movie id is required",
                        HttpStatus.BAD_REQUEST,
                        request);
            } else {
                Optional<Movie> foundMovie = movieRepository.findById(movie.getId());
                if(foundMovie.isPresent()) {
                    toRemoveMovie = foundMovie.get();
                } else {
                    return new GlobalResponseHandler().handleResponse(
                            "Movie not found",
                            HttpStatus.NOT_FOUND,
                            request);
                }
            }
            if(foundPrefereceList.get().getMovies().contains(toRemoveMovie)) {
                foundPrefereceList.get().getMovies().remove(toRemoveMovie);
                preferenceListRepository.save(foundPrefereceList.get());
                return new GlobalResponseHandler().handleResponse(
                        "Movie removed from the preference list",
                        foundPrefereceList.get(),
                        HttpStatus.OK,
                        request);
            } else {
                return new GlobalResponseHandler().handleResponse(
                        "Movie not in the preference preference list",
                        HttpStatus.BAD_REQUEST,
                        request);
            }
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Preference List with id " + Id + " not found",
                    HttpStatus.NOT_FOUND,
                    request);
        }
    }

}























