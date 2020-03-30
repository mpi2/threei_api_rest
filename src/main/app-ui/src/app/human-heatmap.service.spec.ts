import { TestBed } from '@angular/core/testing';

import { HumanHeatmapService } from './human-heatmap.service';

describe('HumanHeatmapService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: HumanHeatmapService = TestBed.get(HumanHeatmapService);
    expect(service).toBeTruthy();
  });
});
