package bd.edu.seu.gamesclub.service.impl;

import bd.edu.seu.gamesclub.dto.CommitteeMemberRequest;
import bd.edu.seu.gamesclub.dto.CommitteeMemberResponse;
import bd.edu.seu.gamesclub.entity.CommitteeMember;
import bd.edu.seu.gamesclub.entity.enums.CommitteeType;
import bd.edu.seu.gamesclub.exception.ResourceNotFoundException;
import bd.edu.seu.gamesclub.mapper.CommitteeMapper;
import bd.edu.seu.gamesclub.repository.CommitteeMemberRepository;
import bd.edu.seu.gamesclub.service.CommitteeService;
import bd.edu.seu.gamesclub.service.MediaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Default {@link CommitteeService}. */
@Service
@Transactional(readOnly = true)
public class CommitteeServiceImpl implements CommitteeService {

    private final CommitteeMemberRepository committeeRepository;
    private final MediaService mediaService;

    public CommitteeServiceImpl(CommitteeMemberRepository committeeRepository, MediaService mediaService) {
        this.committeeRepository = committeeRepository;
        this.mediaService = mediaService;
    }

    @Override
    public List<CommitteeMemberResponse> getActiveByType(CommitteeType type) {
        return committeeRepository.findByCommitteeTypeAndActiveTrueOrderByDisplayOrderAsc(type)
                .stream().map(CommitteeMapper::toResponse).toList();
    }

    @Override
    public List<CommitteeMemberResponse> getAllByType(CommitteeType type) {
        return committeeRepository.findByCommitteeTypeOrderByDisplayOrderAsc(type)
                .stream().map(CommitteeMapper::toResponse).toList();
    }

    @Override
    public List<CommitteeMemberResponse> searchByName(String name) {
        return committeeRepository.findByNameContainingIgnoreCase(name)
                .stream().map(CommitteeMapper::toResponse).toList();
    }

    @Override
    public CommitteeMemberResponse getById(Long id) {
        return CommitteeMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public CommitteeMemberResponse create(CommitteeMemberRequest request) {
        CommitteeMember member = new CommitteeMember();
        CommitteeMapper.apply(member, request);
        member.setPhoto(mediaService.getReference(request.photoMediaId()));
        return CommitteeMapper.toResponse(committeeRepository.save(member));
    }

    @Override
    @Transactional
    public CommitteeMemberResponse update(Long id, CommitteeMemberRequest request) {
        CommitteeMember member = findEntity(id);
        CommitteeMapper.apply(member, request);
        member.setPhoto(mediaService.getReference(request.photoMediaId()));
        return CommitteeMapper.toResponse(committeeRepository.save(member));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        committeeRepository.delete(findEntity(id));
    }

    private CommitteeMember findEntity(Long id) {
        return committeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Committee member", "id", id));
    }
}
