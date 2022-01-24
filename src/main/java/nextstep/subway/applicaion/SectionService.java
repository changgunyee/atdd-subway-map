package nextstep.subway.applicaion;

import com.sun.jdi.request.DuplicateRequestException;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.SectionResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.DuplicationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

	private final SectionRepository sectionRepository;
	private final LineRepository lineRepository;
	private final StationRepository stationRepository;

	public SectionService(SectionRepository sectionRepository,
												LineRepository lineRepository,
												StationRepository stationRepository
	) {
		this.sectionRepository = sectionRepository;
		this.lineRepository = lineRepository;
		this.stationRepository = stationRepository;
	}

	public SectionResponse createSection(final Long id, final SectionRequest sectionRequest) {
		Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
		Station upStation =
						stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(RuntimeException::new);
		Station downStation =
						stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(RuntimeException::new);
		verifyStationsRelation(upStation);
		Section section = sectionRepository.save(Section.of(line, upStation, downStation, sectionRequest.getDistance()));

		return SectionResponse.of(section.getId(), upStation.getId(), downStation.getId(), section.getDistance());
	}

	private void verifyStationsRelation(final Station upStation) {
		sectionRepository.findSectionByUpStation(upStation)
						.orElseThrow(()-> new DuplicationException("새로운 구간의 상행역은 이미 등록되어있습니다."));
	}
}
