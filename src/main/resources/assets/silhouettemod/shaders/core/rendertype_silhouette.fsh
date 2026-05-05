#version 150

uniform sampler2D Sampler0;
uniform vec4 SilhouetteColor;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 source = texture(Sampler0, texCoord0);
    if (source.a < 0.1) {
        discard;
    }
    fragColor = SilhouetteColor;
}
