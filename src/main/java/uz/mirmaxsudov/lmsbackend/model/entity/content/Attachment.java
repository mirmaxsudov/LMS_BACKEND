package uz.mirmaxsudov.lmsbackend.model.entity.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.mirmaxsudov.lmsbackend.model.entity.base.BaseEntity;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String storedName;

    @Column(nullable = false)
    private String path;

    private String url;

    @Column(nullable = false)
    private String extension;
}
